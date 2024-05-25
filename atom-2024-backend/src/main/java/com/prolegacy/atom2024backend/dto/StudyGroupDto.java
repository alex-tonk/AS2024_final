package com.prolegacy.atom2024backend.dto;

import com.prolegacy.atom2024backend.entities.ids.StudyGroupId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudyGroupDto {
    StudyGroupId id;
    List<TutorWithCourseDto> tutors;
    List<StudentInGroupDto> students;
}
