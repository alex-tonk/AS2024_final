package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.StudentInGroupDto;
import com.prolegacy.atom2024backend.entities.ids.StudentInGroupId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
    private StudyGroup studyGroup;
    @ManyToOne
    @MapsId("studentId")
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
