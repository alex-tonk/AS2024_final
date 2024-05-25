package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.dto.StudentDto;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private StudentId id;
    @OneToOne
    private User user;

    public Student(User user, StudentDto studentDto) {
        this.user = user;
        update(studentDto);
    }

    public void update(StudentDto studentDto) {
    }
}
