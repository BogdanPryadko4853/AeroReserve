package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.CityEntity;
import com.bogdan.aeroreserve.entity.CountryEntity;
import com.bogdan.aeroreserve.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public CityEntity createCity(String name, String iataCode, CountryEntity country) {
        CityEntity city = new CityEntity();
        city.setName(name);
        city.setIataCode(iataCode);
        city.setCountry(country);
        return cityRepository.save(city);
    }

    public List<CityEntity> getAllCities() {
        return cityRepository.findAll();
    }

    public Optional<CityEntity> getCityByIataCode(String iataCode) {
        return cityRepository.findByIataCode(iataCode);
    }

    public Optional<CityEntity> getCityByName(String name) {
        return cityRepository.findByName(name);
    }
}