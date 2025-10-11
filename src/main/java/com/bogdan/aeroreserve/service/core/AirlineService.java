package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.AirlineEntity;
import com.bogdan.aeroreserve.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirlineService {
    private final AirlineRepository airlineRepository;

    public AirlineEntity createAirline(String name, String code, String country) {
        if (airlineRepository.existsByName(name)) {
            throw new RuntimeException("Airline with name '" + name + "' already exists");
        }
        if (airlineRepository.existsByCode(code)) {
            throw new RuntimeException("Airline with code '" + code + "' already exists");
        }

        AirlineEntity airline = new AirlineEntity(name, code, country);
        return airlineRepository.save(airline);
    }

    public AirlineEntity createAirline(String name, String code, String country, String logoUrl) {
        if (airlineRepository.existsByName(name)) {
            throw new RuntimeException("Airline with name '" + name + "' already exists");
        }
        if (airlineRepository.existsByCode(code)) {
            throw new RuntimeException("Airline with code '" + code + "' already exists");
        }

        AirlineEntity airline = new AirlineEntity(name, code, country, logoUrl);
        return airlineRepository.save(airline);
    }

    public List<AirlineEntity> getAllAirlines() {
        return airlineRepository.findAll();
    }

    public Optional<AirlineEntity> getAirlineById(Long id) {
        return airlineRepository.findById(id);
    }

    public Optional<AirlineEntity> getAirlineByCode(String code) {
        return airlineRepository.findByCode(code);
    }
}