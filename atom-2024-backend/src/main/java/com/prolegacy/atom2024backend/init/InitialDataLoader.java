package com.prolegacy.atom2024backend.init;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import com.prolegacy.atom2024backend.entities.Lesson;
import com.prolegacy.atom2024backend.entities.Task;
import com.prolegacy.atom2024backend.entities.Topic;
import com.prolegacy.atom2024backend.entities.Trait;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import com.prolegacy.atom2024backend.repositories.LessonRepository;
import com.prolegacy.atom2024backend.repositories.TaskRepository;
import com.prolegacy.atom2024backend.repositories.TopicRepository;
import com.prolegacy.atom2024backend.repositories.TraitRepository;
import com.prolegacy.atom2024backend.services.FileUploadService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

@Log4j2
@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 110)
public class InitialDataLoader implements ApplicationRunner {

    private static final String dataFolderName = "data";
    private static final String lessonSubfolderName = "lessons";
    private static final String topicPrefix = "topic";
    private static final String traitsFilename = "traits.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TraitRepository traitRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, Lesson> lessonsByCode = new HashMap<>();
        Map<String, Task> tasksByCode = new HashMap<>();
        Map<String, Trait> traitsByCode = new HashMap<>();
        List<Topic> topics = new ArrayList<>();

        try {
            loadTraits(traitsByCode);
        } catch (Exception e) {
            log.warn("Ошибка чтения тегов", e);
        }

        try {
            File lessonsSubfolder = new File(dataFolderName + File.separator + lessonSubfolderName);
            File[] topicFiles = lessonsSubfolder.listFiles((dir, name) -> name.startsWith(topicPrefix));
            if (!lessonsSubfolder.exists()) {
                throw new BusinessLogicException("Не найдена папка с данными обучения");
            }
            if (topicFiles == null || topicFiles.length == 0) {
                throw new BusinessLogicException("Не найдено ни одной темы обучения");
            }
            for (File topicFile : topicFiles) {
                try {
                    JsonNode topicJson = mapper.readTree(topicFile);
                    Topic topic = this.constructTopic(
                            topicJson,
                            lessonsByCode,
                            tasksByCode,
                            traitsByCode
                    );
                    topicRepository.save(topic);
                    topics.add(topic);
                } catch (Exception exception) {
                    throw new BusinessLogicException("Ошибка чтения темы обучения из файла %s".formatted(topicFile.getName()), exception);
                }
            }
        } catch (Exception exception) {
            log.warn("Ошибка загрузки данных обучения", exception);
        }

        if (topics.isEmpty()) {
            throw new IllegalStateException("Не удалось загрузить ни одной темы обучения");
        }
    }

    private void loadTraits(Map<String, Trait> traitsByCode) {
        File traitsFile = new File(dataFolderName + File.separator + lessonSubfolderName + File.separator + traitsFilename);
        if (!traitsFile.exists()) {
            throw new BusinessLogicException("Файл %s не существует".formatted(traitsFile.getName()));
        }
        try {
            JsonNode traitsJson = mapper.readTree(traitsFile);
            if (!traitsJson.isArray()) {
                throw new BusinessLogicException("Некорректный формат списка тегов");
            }
            for (JsonNode traitJson : traitsJson) {
                try {
                    Trait trait = constructTrait(traitJson);
                    traitsByCode.put(trait.getCode(), trait);
                    traitRepository.save(trait);
                } catch (Exception e) {
                    log.warn("Ошибка чтения тега");
                }
            }
        } catch (Exception exception) {
            throw new BusinessLogicException("Ошибка чтения тегов из файла %s".formatted(traitsFile.getName()), exception);
        }
    }

    private Trait constructTrait(JsonNode traitJson) {
        if (traitJson == null || !traitJson.isObject()) {
            throw new BusinessLogicException("Некорректный формат данных");
        }

        String code = unwrapString(traitJson.get("code"));
        if (code == null) {
            throw new BusinessLogicException("Некорректный код тега");
        }

        String name = unwrapString(traitJson.get("name"));
        String description = unwrapString(traitJson.get("description"));
        return new Trait(
                code,
                name,
                description
        );
    }

    private Topic constructTopic(JsonNode topicJson,
                                 Map<String, Lesson> lessonsByCode,
                                 Map<String, Task> tasksByCode,
                                 Map<String, Trait> traitsByCode) {
        if (topicJson == null || !topicJson.isObject()) {
            throw new BusinessLogicException("Некорректный формат данных");
        }

        String code = unwrapString(topicJson.get("code"));
        String title = unwrapString(topicJson.get("title"));
        String description = unwrapString(topicJson.get("description"));

        Topic result = new Topic(
                code,
                title,
                description
        );

        List<String> lessonCodes = Optional.ofNullable(topicJson.get("lessons"))
                .map(json -> {
                    List<String> codes = new ArrayList<>();
                    if (!json.isArray()) {
                        log.warn("Некорректный формат списка уроков темы обучения [код = %s]".formatted(result.getCode()));
                        return codes;
                    }
                    for (JsonNode codeJson : json) {
                        Optional.ofNullable(unwrapString(codeJson))
                                .ifPresent(codes::add);
                    }
                    return codes;
                }).orElseGet(ArrayList::new);
        result.getLessons().addAll(
                lessonCodes.stream()
                        .map(lessonCode -> constructLesson(lessonCode, lessonsByCode, tasksByCode, traitsByCode))
                        .filter(Objects::nonNull)
                        .toList()
        );

        List<String> traitCodes = Optional.ofNullable(topicJson.get("traits"))
                .map(json -> {
                    List<String> codes = new ArrayList<>();
                    if (!json.isArray()) {
                        log.warn("Некорректный формат списка тегов темы обучения [код = %s]".formatted(result.getCode()));
                        return codes;
                    }
                    for (JsonNode codeJson : json) {
                        Optional.ofNullable(unwrapString(codeJson))
                                .ifPresent(codes::add);
                    }
                    return codes;
                }).orElseGet(ArrayList::new);
        result.getTraits().addAll(
                traitCodes.stream()
                        .map(traitsByCode::get)
                        .filter(Objects::nonNull)
                        .toList()
        );

        return result;
    }

    private Lesson constructLesson(String code,
                                   Map<String, Lesson> lessonsByCode,
                                   Map<String, Task> tasksByCode,
                                   Map<String, Trait> traitsByCode) {
        if (lessonsByCode.containsKey(code)) {
            return lessonsByCode.get(code);
        }
        try {
            File lessonFile = new File(dataFolderName + File.separator + lessonSubfolderName + File.separator + code + File.separator + code + ".json");
            JsonNode lessonJson = mapper.readTree(lessonFile);
            String content = unwrapString(lessonJson.get("content"));
            List<String> traitCodes = Optional.ofNullable(lessonJson.get("traits"))
                    .map(json -> {
                        List<String> codes = new ArrayList<>();
                        if (!json.isArray()) {
                            log.warn("Некорректный формат списка тегов урока [код = %s]".formatted(code));
                            return codes;
                        }
                        for (JsonNode codeJson : json) {
                            Optional.ofNullable(unwrapString(codeJson))
                                    .ifPresent(codes::add);
                        }
                        return codes;
                    }).orElseGet(ArrayList::new);
            List<Pair<String, String>> supplements = Optional.ofNullable(lessonJson.get("supplement"))
                    .map(json -> {
                        List<Pair<String, String>> s = new ArrayList<>();
                        if (!json.isArray()) {
                            log.warn("Некорректный формат списка файловых вложений в уроке [код = %s]".formatted(code));
                            return s;
                        }
                        for (JsonNode supplementJson : json) {
                            Pair<String, String> supplement = Pair.of(
                                    unwrapString(supplementJson.get("title")),
                                    unwrapString(supplementJson.get("file"))
                            );
                            if (supplement.getKey() != null && supplement.getValue() != null) {
                                s.add(supplement);
                            }
                        }
                        return s;
                    }).orElseGet(ArrayList::new);
            for (Pair<String, String> s : supplements) {
                try {
                    File supplementFile = new File(dataFolderName + File.separator + lessonSubfolderName + File.separator + code + File.separator + s.getValue());
                    FileId fileId = fileUploadService.createFromFile(supplementFile).getId();
                    content = Optional.ofNullable(content)
                            .map(c -> c.replaceAll("!\\[%s\\]\\(%s\\)".formatted(s.getKey(), s.getValue()), "[%s](<baseUrl>/lessons/files/%s)".formatted(s.getKey(), fileId))).orElse(content);
                } catch (Exception e) {
                    log.warn("Ошибка загрузки вложения в уроке [код урока = %s, имя файла = %s]".formatted(code, s.getValue()));
                }
            }

            List<String> taskCodes = Optional.ofNullable(lessonJson.get("tasks"))
                    .map(json -> {
                        List<String> codes = new ArrayList<>();
                        if (!json.isArray()) {
                            log.warn("Некорректный формат списка заданий урока [код = %s]".formatted(code));
                            return codes;
                        }
                        for (JsonNode codeJson : json) {
                            Optional.ofNullable(unwrapString(codeJson))
                                    .ifPresent(codes::add);
                        }
                        return codes;
                    }).orElseGet(ArrayList::new);

            var result = new Lesson(
                    code,
                    unwrapString(lessonJson.get("title")),
                    content,
                    unwrapString(lessonJson.get("author"))
            );

            result.getTraits().addAll(
                    traitCodes.stream()
                            .map(traitsByCode::get)
                            .filter(Objects::nonNull)
                            .toList()
            );

            result.getTasks().addAll(
                    taskCodes.stream()
                            .map(taskCode -> constructTask(taskCode, tasksByCode))
                            .filter(Objects::nonNull)
                            .toList()
            );

            this.lessonRepository.save(result);
            lessonsByCode.put(result.getCode(), result);
            return result;
        } catch (Exception e) {
            log.warn("Ошибка загрузки урока [код = %s]".formatted(code), e);
            return null;
        }
    }

    private Task constructTask(String code, Map<String, Task> tasksByCode) {
        if (tasksByCode.containsKey(code)) {
            return tasksByCode.get(code);
        }
        try {
            File taskFile = new File(dataFolderName + File.separator + lessonSubfolderName + File.separator + code + File.separator + code + ".json");
            JsonNode taskJson = mapper.readTree(taskFile);
            String content = unwrapString(taskJson.get("content"));

            List<Pair<String, String>> supplements = Optional.ofNullable(taskJson.get("supplement"))
                    .map(json -> {
                        List<Pair<String, String>> s = new ArrayList<>();
                        if (!json.isArray()) {
                            log.warn("Некорректный формат списка файловых вложений в задании [код = %s]".formatted(code));
                            return s;
                        }
                        for (JsonNode supplementJson : json) {
                            Pair<String, String> supplement = Pair.of(
                                    unwrapString(supplementJson.get("title")),
                                    unwrapString(supplementJson.get("file"))
                            );
                            if (supplement.getKey() != null && supplement.getValue() != null) {
                                s.add(supplement);
                            }
                        }
                        return s;
                    }).orElseGet(ArrayList::new);
            for (Pair<String, String> s : supplements) {
                try {
                    File supplementFile = new File(dataFolderName + File.separator + lessonSubfolderName + File.separator + code + File.separator + s.getValue());
                    FileId fileId = fileUploadService.createFromFile(supplementFile).getId();
                    content = Optional.ofNullable(content)
                            .map(c -> c.replaceAll("!\\[%s\\]\\(%s\\)".formatted(s.getKey(), s.getValue()), "[%s](<baseUrl>/tasks/files/%s)".formatted(s.getKey(), fileId))).orElse(content);
                } catch (Exception e) {
                    log.warn("Ошибка загрузки вложения в уроке [код урока = %s, имя файла = %s]".formatted(code, s.getValue()));
                }
            }

            BigDecimal difficulty = Optional.ofNullable(taskJson.get("difficulty"))
                    .map(json -> {
                        if (json.isNumber()) {
                            return new BigDecimal(json.toString());
                        } else {
                            return null;
                        }
                    }).orElse(null);

            Integer time = Optional.ofNullable(taskJson.get("time"))
                    .map(json -> {
                        if (json.isNumber()) {
                            return json.asInt();
                        } else {
                            return null;
                        }
                    }).orElse(null);

            var result = new Task(
                    code,
                    unwrapString(taskJson.get("title")),
                    content,
                    difficulty,
                    time
            );

            taskRepository.save(result);
            tasksByCode.put(result.getCode(), result);

            return result;
        } catch (Exception e) {
            log.warn("Ошибка загрузки задания [код = %s]".formatted(code), e);
            return null;
        }
    }

    private String unwrapString(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode)
                .map(json -> {
                    if (json.isTextual()) {
                        return json.asText();
                    } else {
                        return json.toString();
                    }
                }).orElse(null);
    }

}
