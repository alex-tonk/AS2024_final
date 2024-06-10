package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.common.auth.exceptions.UserNotFoundException;
import com.prolegacy.atom2024backend.common.auth.repositories.RoleRepository;
import com.prolegacy.atom2024backend.common.auth.repositories.UserRepository;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.TutorDto;
import com.prolegacy.atom2024backend.entities.Tutor;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
import com.prolegacy.atom2024backend.exceptions.TutorNotFoundException;
import com.prolegacy.atom2024backend.readers.TutorReader;
import com.prolegacy.atom2024backend.repositories.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class TutorService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorReader tutorReader;

    public TutorDto createTutor(TutorDto tutorDto) {
        if (tutorDto.getUser() == null) {
            throw new BusinessLogicException("Не указан пользователь");
        }

        User user = userRepository.findByEmail(tutorDto.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException(tutorDto.getUser().getEmail()));

        user.getRoles().add(roleRepository.findByName("ROLE_tutor")
                .orElseThrow(() -> new BusinessLogicException("Роль \"преподаватель\""))
        );

        Tutor tutor = tutorRepository.save(new Tutor(user, tutorDto));
        return tutorReader.getTutor(tutor.getId());
    }

    public TutorDto updateTutor(TutorId tutorId, TutorDto tutorDto) {
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new TutorNotFoundException(tutorId));
        tutor.update(tutorDto);
        return tutorReader.getTutor(tutorId);
    }
}
