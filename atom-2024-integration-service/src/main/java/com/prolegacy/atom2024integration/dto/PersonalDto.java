package com.prolegacy.atom2024integration.dto;

import com.prolegacy.atom2024integration.entities.ids.PersonalId;
import lombok.Data;

@Data
public class PersonalDto {
    PersonalId id;
    String caption;
}
