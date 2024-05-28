package com.prolegacy.atom2024backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CourseWithTutorsDto {
    StudyGroupDto studyGroup;
    CourseDto course;
    List<TutorDto> tutors;
}
