package com.prolegacy.atom2024integration.entities;

import com.prolegacy.atom2024integration.entities.ids.PersonalId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(schema = "dictionaries")
@Data
@Getter
public class Personal {
    @Id
    PersonalId id;
    String caption;
}
