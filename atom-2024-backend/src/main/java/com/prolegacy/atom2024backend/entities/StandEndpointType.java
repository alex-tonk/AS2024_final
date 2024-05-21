package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.parsing.Metadata;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.dto.StandEndpointTypeDto;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointTypeId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Optional;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode
public class StandEndpointType {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private StandEndpointTypeId id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Metadata inMeta;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Metadata outMeta;

    public StandEndpointType(StandEndpointDto dto) {
        this.inMeta = Optional.ofNullable(dto.getStandEndpointType()).map(StandEndpointTypeDto::getInMeta).orElse(null);
        this.outMeta = Optional.ofNullable(dto.getStandEndpointType()).map(StandEndpointTypeDto::getOutMeta).orElse(null);
    }
}
