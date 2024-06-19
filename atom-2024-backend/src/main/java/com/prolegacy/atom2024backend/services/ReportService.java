package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.entities.id.UserId;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.entities.Lesson;
import com.prolegacy.atom2024backend.entities.ids.TopicId;
import com.prolegacy.atom2024backend.exceptions.TopicNotFoundException;
import com.prolegacy.atom2024backend.readers.AttemptReader;
import com.prolegacy.atom2024backend.readers.LessonReader;
import com.prolegacy.atom2024backend.repositories.TopicRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Value("classpath:diploma.jrxml")
    private Resource diploma;

    @Value("classpath:diploma_spec.jrxml")
    private Resource diplomaSpec;

    @Value("classpath:background.png")
    private Resource background;

    @Value("classpath:logo.png")
    private Resource logo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AttemptReader attemptReader;

    @Autowired
    private TopicRepository topicRepository;



    public Resource createDiploma(UserId userId, TopicId topicId) {
        this.validateUserFinishedTopic(userId, topicId);
        try {
            return jasperToPdf(List.of(createDiplomaInternal(userId, topicId)));
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка генерации печатной формы", e);
        }
    }

    public Resource createDiplomaSpec(UserId userId, TopicId topicId) {
        this.validateUserFinishedTopic(userId, topicId);
        try {
            return jasperToPdf(List.of(createDiplomaSpecInternal(userId, topicId)));
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка генерации печатной формы", e);
        }
    }

    public Resource createDiplomaFull(UserId userId, TopicId topicId) {
        this.validateUserFinishedTopic(userId, topicId);
        try {
            return jasperToPdf(List.of(createDiplomaInternal(userId, topicId), createDiplomaSpecInternal(userId, topicId)));
        } catch (Exception e) {
            throw new BusinessLogicException("Ошибка генерации печатной формы", e);
        }
    }

    private JasperPrint createDiplomaInternal(UserId userId, TopicId topicId) throws IOException, JRException, SQLException {
        InputStream inputStream = diploma.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Image backgroundImage = ImageIO.read(background.getInputStream());
        Image logoImage = ImageIO.read(logo.getInputStream());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId.longValue());
        parameters.put("topicId", topicId.longValue());
        parameters.put("background", backgroundImage);
        parameters.put("logo", logoImage);
        try (var connection = dataSource.getConnection()) {
            return JasperFillManager.fillReport(jasperReport, parameters, connection);
        }
    }

    private JasperPrint createDiplomaSpecInternal(UserId userId, TopicId topicId) throws IOException, JRException, SQLException {
        InputStream inputStream = diplomaSpec.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Image logoImage = ImageIO.read(logo.getInputStream());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId.longValue());
        parameters.put("topicId", topicId.longValue());
        parameters.put("logo", logoImage);
        try (var connection = dataSource.getConnection()) {
            return JasperFillManager.fillReport(jasperReport, parameters, connection);
        }
    }

    private Resource jasperToPdf(List<JasperPrint> prints) throws IOException, JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        SimplePdfExporterConfiguration exportConfig
                = new SimplePdfExporterConfiguration();
        exportConfig.setEncrypted(true);
        exporter.setConfiguration(exportConfig);
        exporter.setExporterInput(SimpleExporterInput.getInstance(prints));
        try (var output = new ByteArrayOutputStream()) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
            exporter.exportReport();
            return new ByteArrayResource(output.toByteArray());
        }
    }

    private void validateUserFinishedTopic(UserId userId, TopicId topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(TopicNotFoundException::new);
        var successfulLastAttempts = attemptReader.getLastAttemptsForTopic(userId, topicId)
                .stream()
                .filter(a -> a.getTutorMark() != null && a.getTutorMark().value.compareTo(BigDecimal.valueOf(3)) >= 0)
                .toList();
        if (successfulLastAttempts.size() != topic.getLessons().stream().map(Lesson::getTasks).mapToLong(Collection::size).sum()) {
            throw new BusinessLogicException("Пользователь ещё не завершил тему");
        }
    }
}
