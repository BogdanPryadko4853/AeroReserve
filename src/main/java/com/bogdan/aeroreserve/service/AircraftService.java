package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.AircraftEntity;
import com.bogdan.aeroreserve.repository.AircraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AircraftService {
    private final AircraftRepository aircraftRepository;

    public AircraftEntity createAircraft(String model, String manufacturer, int economy, int business, int first) {
        if (aircraftRepository.existsByModel(model)) {
            throw new RuntimeException("Aircraft model already exists: " + model);
        }

        AircraftEntity aircraft = new AircraftEntity(model, manufacturer, economy, business, first);
        return aircraftRepository.save(aircraft);
    }

    public List<AircraftEntity> getAllAircrafts() {
        return aircraftRepository.findAll();
    }

    public Optional<AircraftEntity> getAircraftById(Long id) {
        return aircraftRepository.findById(id);
    }
}