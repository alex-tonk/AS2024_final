package com.prolegacy.atom2024integration.services;

import com.prolegacy.atom2024integration.dto.ThermoDto;
import com.prolegacy.atom2024integration.entities.Thermo;
import com.prolegacy.atom2024integration.readers.ThermoReader;
import com.prolegacy.atom2024integration.repositories.ThermoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThermoService {
    @Autowired
    private ThermoRepository thermoRepository;
    @Autowired
    private ThermoReader thermoReader;

    @Transactional
    public ThermoDto createThermo(ThermoDto thermoDto) {
        Thermo thermo = thermoRepository.save(new Thermo(thermoDto));

        return thermoReader.getThermo(thermo.getId());
    }
}
