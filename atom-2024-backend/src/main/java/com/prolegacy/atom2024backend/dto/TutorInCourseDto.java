package com.prolegacy.atom2024backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TutorInCourseDto {
    StudyGroupDto studyGroup;
    CourseDto course;
    TutorDto tutor;
}
