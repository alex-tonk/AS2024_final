package com.prolegacy.atom2024backend.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolegacy.atom2024backend.common.exceptions.BusinessLogicException;
import com.prolegacy.atom2024backend.common.util.InitializationOrder;
import com.prolegacy.atom2024backend.dto.ProductDto;
import com.prolegacy.atom2024backend.dto.StandDto;
import com.prolegacy.atom2024backend.entities.Product;
import com.prolegacy.atom2024backend.entities.Stand;
import com.prolegacy.atom2024backend.entities.StandEndpoint;
import com.prolegacy.atom2024backend.entities.StandEndpointType;
import com.prolegacy.atom2024backend.entities.enums.ComputationType;
import com.prolegacy.atom2024backend.entities.ids.StandId;
import com.prolegacy.atom2024backend.repositories.ProductRepository;
import com.prolegacy.atom2024backend.repositories.StandEndpointTypeRepository;
import com.prolegacy.atom2024backend.repositories.StandRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@Order(InitializationOrder.ROLE_GENERATOR + 100)
@Log4j2
public class StandGenerator implements ApplicationRunner {

    @Value("${stands.json:#{null}}")
    String standsJson;

    @Autowired
    private StandRepository standRepository;
    @Autowired
    private StandEndpointTypeRepository standEndpointTypeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<Stand> stands = this.standRepository.findAll();
        if (stands.isEmpty()) {
            try {
                if (standsJson == null) {
                    throw new RuntimeException();
                }
                ObjectMapper objectMapper = new ObjectMapper();
                stands = objectMapper.readValue(standsJson, new TypeReference<>() {
                });
                stands.forEach(s -> Optional.ofNullable(s.getEndpoints()).orElseGet(ArrayList::new).forEach(e -> e.setStand(s)));
                List<StandEndpointType> endpointTypes = stands.stream()
                        .flatMap(s -> Optional.ofNullable(s.getEndpoints()).stream().flatMap(Collection::stream))
                        .map(StandEndpoint::getStandEndpointType)
                        .collect(Collectors.toList());
                standEndpointTypeRepository.saveAll(endpointTypes);
                stands = standRepository.saveAll(stands);
                var emulatedStand = new Stand(
                        StandDto.builder()
                                .name("Эмуляция")
                                .description("Стенд для эмуляции")
                                .computationType(ComputationType.EMULATED)
                                .build()
                );
                standRepository.save(emulatedStand);
            } catch (Exception e) {
                throw new BusinessLogicException("Некорректная конфигурация. Проверьте правильность заполнения параметра stands.json в docker-compose.yml");
            }
        }

        Set<Pair<Long, StandId>> idPairs = new HashSet<>();
        productRepository.findAll()
                .forEach(
                        p -> idPairs.add(ImmutablePair.of(p.getId(), p.getStandId()))
                );

        stands.stream()
                .filter(s -> s.getUrl() != null)
                .forEach(stand -> {
                    try {

                        URI uri = UriComponentsBuilder
                                .fromHttpUrl(stand.getUrl() + "/dict/products")
                                .build()
                                .toUri();
                        ProductDto[] response = restTemplate.getForObject(uri, ProductDto[].class);

                        if (response == null) return;
                        for (ProductDto productDto : response) {
                            productDto.setStandId(stand.getId());
                        }

                        //noinspection Convert2MethodRef
                        productRepository.saveAll(
                                Arrays.stream(response)
                                        .filter(p -> !idPairs.contains(ImmutablePair.of(p.getId(), p.getStandId())))
                                        .map(dto -> new Product(dto))
                                        .toList()
                        );
                    } catch (Exception e) {
                        log.warn("Предупреждение: не удалось получить ДСЕ от стенда", e);
                    }
                });
    }
}
