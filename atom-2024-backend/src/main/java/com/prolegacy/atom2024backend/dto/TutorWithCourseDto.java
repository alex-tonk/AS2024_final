package com.prolegacy.atom2024backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TutorWithCourseDto {
    StudyGroupDto studyGroup;
    TutorDto tutor;
    CourseDto course;
}
