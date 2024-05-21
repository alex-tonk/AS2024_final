package com.prolegacy.atom2024backend.services;

import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.dto.StandEndpointDto;
import com.prolegacy.atom2024backend.entities.QStand;
import com.prolegacy.atom2024backend.entities.Stand;
import com.prolegacy.atom2024backend.entities.StandEndpoint;
import com.prolegacy.atom2024backend.entities.StandEndpointType;
import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandEndpointId;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import com.prolegacy.atom2024backend.exceptions.StandNotFoundException;
import com.prolegacy.atom2024backend.readers.StandEndpointReader;
import com.prolegacy.atom2024backend.repositories.StandEndpointTypeRepository;
import com.prolegacy.atom2024backend.repositories.StandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.StreamSupport;

@Service
public class StandService {
    @Autowired
    private StandRepository standRepository;
    @Autowired
    private StandEndpointTypeRepository standEndpointTypeRepository;
    @Autowired
    private StandEndpointReader standEndpointReader;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public StandEndpointDto addEmulatedEndpoint(StandEndpointDto standEndpointDto) {
        Stand virtualStand = StreamSupport.stream(
                        standRepository.findAll(QStand.stand.computationType.eq(ComputationType.EMULATED))
                                .spliterator(),
                        false
                )
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException("Отсутствует стенд для проведения виртуальных испытаний"));

        if (standRepository.existsByEndpointsName(standEndpointDto.getName())) {
            throw new BusinessLogicException("Стенд с именем %s уже существует".formatted(standEndpointDto.getName()));
        }

        virtualStand.addEndpoint(standEndpointTypeRepository.save(new StandEndpointType(standEndpointDto)), standEndpointDto);
        virtualStand = standRepository.save(virtualStand);
        return standEndpointReader.getStandEndpoint(virtualStand.getId(), virtualStand.getEndpoints().get(virtualStand.getEndpoints().size() - 1).getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public StandEndpointDto updateVirtualStandEndpoint(StandId standId, StandEndpointId standEndpointId, StandEndpointDto standEndpointDto) {
        Stand stand = standRepository.findById(standId)
                .orElseThrow(StandNotFoundException::new);

        if (!ComputationType.EMULATED.equals(stand.getComputationType())) {
            throw new BusinessLogicException("Нельзя редактировать немодериемый стенд");
        }

        StandEndpoint endpoint = stand.getEndpoint(standEndpointId);
        endpoint.update(standEndpointDto);

        if(!endpoint.getName().equals(standEndpointDto.getName())) {
            if (standRepository.existsByEndpointsName(standEndpointDto.getName())) {
                throw new BusinessLogicException("Стенд с именем %s уже существует".formatted(standEndpointDto.getName()));
            }
        }

        standRepository.save(stand);

        return standEndpointReader.getStandEndpoint(standId, standEndpointId);
    }
}
