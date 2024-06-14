package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.WorkshopId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Workshop {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private WorkshopId id;
}
