package com.prolegacy.atom2024backend.survey.init;

import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import com.prolegacy.atom2024backend.survey.dto.SurveyDto;
import com.prolegacy.atom2024backend.survey.dto.SurveyQuestionDto;
import com.prolegacy.atom2024backend.survey.entities.RadioButtonSurveyQuestion;
import com.prolegacy.atom2024backend.survey.entities.StringSurveyQuestion;
import com.prolegacy.atom2024backend.survey.entities.Survey;
import com.prolegacy.atom2024backend.survey.meta.answers.RadioButtonSurveyQuestionAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.answers.StringSurveyQuestionCorrectAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerMeta;
import com.prolegacy.atom2024backend.survey.meta.common.PredefinedAnswerQuestionMeta;
import com.prolegacy.atom2024backend.survey.repositories.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 101)
public class DebugSurveyGenerator implements ApplicationRunner {
    @Autowired
    private SurveyRepository surveyRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (!surveyRepository.findAll().isEmpty()) {
            return;
        }
        var survey = new Survey(
                SurveyDto.builder()
                        .name("Готов ли ты играть в доту?")
                        .build()
        );
        survey.addQuestion(
                new StringSurveyQuestion(
                        survey,
                        null,
                        SurveyQuestionDto.builder()
                                .wording("Члены пробовал?")
                                .comment("Честно")
                                .build(),
                        new StringSurveyQuestionCorrectAnswerMeta(List.of("Да"))
                )
        );
        survey.addQuestion(
                new RadioButtonSurveyQuestion(
                        survey,
                        null,
                        SurveyQuestionDto.builder()
                                .wording("А хочешь?")
                                .build(),
                        new PredefinedAnswerQuestionMeta(
                                List.of(
                                        new PredefinedAnswerMeta(1L, "Да"),
                                        new PredefinedAnswerMeta(2L, "Нет")
                                )
                        ),
                        new RadioButtonSurveyQuestionAnswerMeta(1L)
                )
        );
        surveyRepository.save(survey);
    }

}
