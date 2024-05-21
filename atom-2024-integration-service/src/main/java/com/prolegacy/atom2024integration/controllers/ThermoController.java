package com.prolegacy.atom2024integration.controllers;

import com.prolegacy.atom2024integration.dto.ThermoDto;
import com.prolegacy.atom2024integration.readers.ThermoReader;
import com.prolegacy.atom2024integration.services.ThermoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("test")
public class ThermoController {
    @Autowired
    private ThermoReader thermoReader;
    @Autowired
    private ThermoService thermoService;

    @PostMapping("barocamera")
    public ThermoDto createThermo(@RequestBody ThermoDto thermoDto) {
        return thermoService.createThermo(thermoDto);
    }

    @GetMapping("{testId}")
    public ThermoDto getThermo(@PathVariable Long testId) {
        return thermoReader.getThermo(testId);
    }
}
