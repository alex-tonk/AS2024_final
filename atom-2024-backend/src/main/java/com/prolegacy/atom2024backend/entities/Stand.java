package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import com.prolegacy.atom2024backend.exceptions.StandEndpointNotFoundException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
public class Stand {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private StandId id;
    private String name;
    private String description;
    private String url;
    @Column(nullable = false, columnDefinition = "boolean default false")
    @Setter
    private Boolean isArchived = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComputationType computationType = ComputationType.REST;

    @OneToMany(mappedBy = "stand", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StandEndpoint> endpoints = new ArrayList<>();

    public Stand(StandDto dto) {
        this.update(dto);
    }

    public void update(StandDto dto) {
        this.url = url;
        this.computationType = dto.getComputationType();
        this.name = dto.getName();
        this.description = dto.getDescription();
    }

    public StandEndpoint addEndpoint(StandEndpointType standEndpointType, StandEndpointDto standEndpointDto) {
        StandEndpoint endpoint = new StandEndpoint(this, standEndpointType, standEndpointDto);
        this.endpoints.add(endpoint);
        return endpoint;
    }

    public StandEndpoint getEndpoint(StandEndpointId standEndpointId) {
        return this.endpoints
                .stream()
                .filter(e -> e.getId().equals(standEndpointId))
                .findFirst()
                .orElseThrow(StandEndpointNotFoundException::new);
    }

    public void archive() {
        this.isArchived = true;
    }

    public void unarchive() {
        this.isArchived = false;
    }
}
