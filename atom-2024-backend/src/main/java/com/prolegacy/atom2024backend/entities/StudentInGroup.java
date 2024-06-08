package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.entities.ids.StudentInGroupId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class StudentInGroup {
    @EmbeddedId
    private StudentInGroupId id;

    @ManyToOne
    @MapsId("studyGroupId")
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;
    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public StudentInGroup(StudyGroup studyGroup, StudentInGroupDto studentInGroupDto) {
        this.id = new StudentInGroupId(studyGroup.getId(), studentInGroupDto.getStudent().getId());
        this.studyGroup = studyGroup;
        update(studentInGroupDto);
    }

    public StudentInGroup(StudyGroup studyGroup, Student student) {
        this.id = new StudentInGroupId(studyGroup.getId(), student.getId());
        this.studyGroup = studyGroup;
        this.student = student;
    }

    public void update(StudentInGroupDto studentInGroupDto) {

    }
}
