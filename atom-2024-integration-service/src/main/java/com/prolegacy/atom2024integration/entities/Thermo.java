package com.prolegacy.atom2024integration.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.prolegacy.atom2024integration.dto.PersonalDto;
import com.prolegacy.atom2024integration.dto.ProductDto;
import com.prolegacy.atom2024integration.dto.ThermoDto;
import com.prolegacy.atom2024integration.entities.ids.PersonalId;
import com.prolegacy.atom2024integration.entities.ids.ProductId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Optional;

@Entity
@Table(schema = "migration")
@Data
@Getter
@NoArgsConstructor
public class Thermo {
    @Id
    @GeneratedValue(generator = "migration.seq_thermo")
    @SequenceGenerator(name = "migration.seq_thermo", sequenceName = "migration.seq_thermo")
    Long id;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "param", columnDefinition = "jsonb")
    JsonNode params;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    JsonNode result;
    Instant registrationDateTime;
    @Column(name = "executionStartDateTime")
    Instant executionStart;
    @Column(name = "executionEndDateTime")
    Instant executionEnd;
    String state = "UNREGISTERED";
    PersonalId registeredBy;
    PersonalId executionBy;
    ProductId productId;

    public Thermo(ThermoDto dto) {
        this.params = dto.getParams();
        this.productId = Optional.ofNullable(dto.getProduct()).map(ProductDto::getId).orElse(null);
    }
}
