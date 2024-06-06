package com.prolegacy.atom2024backend.survey.repositories;

import com.prolegacy.atom2024backend.survey.entities.Survey;
import com.prolegacy.atom2024backend.survey.entities.id.SurveyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, SurveyId> {
}
