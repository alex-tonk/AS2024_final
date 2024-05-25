package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.exceptions.UserNotFoundException;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.StudentDto;
import com.prolegacy.atom2024backend.entities.Student;
import com.prolegacy.atom2024backend.entities.ids.StudentId;
import com.prolegacy.atom2024backend.exceptions.StudentNotFoundException;
import com.prolegacy.atom2024backend.readers.StudentReader;
import com.prolegacy.atom2024backend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentReader studentReader;

    public StudentDto createStudent(StudentDto studentDto) {
        if (studentDto.getUser() == null) {
            throw new BusinessLogicException("Не указан пользователь");
        }

        User user = userRepository.findByEmail(studentDto.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException(studentDto.getUser().getEmail()));

        Student student = studentRepository.save(new Student(user, studentDto));
        return studentReader.getStudent(student.getId());
    }

    public StudentDto updateStudent(StudentId studentId, StudentDto studentDto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        student.update(studentDto);
        return studentReader.getStudent(studentId);
    }
}
