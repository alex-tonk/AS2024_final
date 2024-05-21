package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import jakarta.persistence.*;
import lombok.*;
import org.assertj.core.util.Strings;

import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class StandEndpoint {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private StandEndpointId id;

    @OneToOne
    @JoinColumn(name = "stand_endpoint_type_id", nullable = false)
    private StandEndpointType standEndpointType;

    @Column(unique = true, nullable = false)
    private String name;
    private String description;
    private String url;
    @Column(columnDefinition = "text")
    private String jsCode;

    @ManyToOne
    @JoinColumn(name = "stand_id", nullable = false)
    @Setter(AccessLevel.PUBLIC)
    private Stand stand;

    public StandEndpoint(Stand stand, StandEndpointType standEndpointType, StandEndpointDto dto) {
        this.stand = stand;
        this.standEndpointType = standEndpointType;
        this.update(dto);
    }

    public void update(StandEndpointDto dto) {
        if(Strings.isNullOrEmpty(dto.getName())) {
            throw new BusinessLogicException("Имя стенда не должно быть пустым");
        }

        this.name = dto.getName();
        this.description = dto.getDescription();

        if(this.stand.getComputationType() == ComputationType.REST
                && Strings.isNullOrEmpty(dto.getUrl())) {
            throw new BusinessLogicException("Адрес API не может быть пустым");
        }
        this.url = dto.getUrl();

        if(this.stand.getComputationType() == ComputationType.EMULATED
                && Strings.isNullOrEmpty(dto.getJsCode())) {
            throw new BusinessLogicException("Адрес API не может быть пустым");
        }
        this.jsCode = dto.getJsCode();
    }
}
