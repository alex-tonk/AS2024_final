package com.prolegacy.atom2024backend.controllers;

import com.prolegacy.atom2024backend.common.annotation.TypescriptEndpoint;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.dto.StandEndpointTypeDto;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import com.prolegacy.atom2024backend.readers.StandEndpointReader;
import com.prolegacy.atom2024backend.readers.StandEndpointTypeReader;
import com.prolegacy.atom2024backend.readers.StandReader;
import com.prolegacy.atom2024backend.services.StandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("stands")
@TypescriptEndpoint
public class StandController {
    @Autowired
    private StandReader standReader;
    @Autowired
    private StandService standService;
    @Autowired
    private StandEndpointReader standEndpointReader;
    @Autowired
    private StandEndpointTypeReader standEndpointTypeReader;

    @GetMapping
    public List<StandDto> getStands() {
        return standReader.getStands();
    }

    @GetMapping("{id}")
    public StandDto getStand(@PathVariable StandId id) {
        return standReader.getStand(id);
    }

    @GetMapping("endpoints")
    public List<StandEndpointDto> getAllStandEndpoints() {
        return standEndpointReader.getAllStandEndpoints();
    }

    @GetMapping("endpoints/virtual")
    public List<StandEndpointDto> getVirtualEndpoints() {
        return standEndpointReader.getVirtualEndpoints();
    }

    @GetMapping("types")
    public List<StandEndpointTypeDto> getStandEndpointTypes() {
        return standEndpointTypeReader.getEndpointTypes();
    }

    @GetMapping("{id}/endpoints")
    public List<StandEndpointDto> getStandEndpoints(@PathVariable StandId id) {
        return standEndpointReader.getStandEndpoints(id);
    }

    @GetMapping("{id}/endpoints/{endpointId}")
    public StandEndpointDto getStandEndpoint(@PathVariable StandId id,
                                             @PathVariable StandEndpointId endpointId) {
        return standEndpointReader.getStandEndpoint(id, endpointId);
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public StandEndpointDto createVirtualStandEndpoint(@RequestBody StandEndpointDto standEndpointDto) {
        return standService.addEmulatedEndpoint(standEndpointDto);
    }
    @PreAuthorize("hasRole('admin')")
    @PutMapping
    public StandEndpointDto updateVirtualStandEndpoint(
            @RequestParam StandId standId,
            @RequestParam StandEndpointId standEndpointId,
            @RequestBody StandEndpointDto standEndpointDto
    ) {
        return standService.updateVirtualStandEndpoint(standId, standEndpointId, standEndpointDto);
    }
}
