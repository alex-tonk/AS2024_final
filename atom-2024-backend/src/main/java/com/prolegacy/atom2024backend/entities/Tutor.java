package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.auth.entities.User;
import com.prolegacy.atom2024backend.dto.TutorDto;
import com.prolegacy.atom2024backend.entities.ids.TutorId;
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
public class Tutor {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TutorId id;
    @OneToOne
    private User user;

    public Tutor(User user, TutorDto tutorDto) {
        this.user = user;
        update(tutorDto);
    }

    public void update(TutorDto tutorDto) {
    }
}
