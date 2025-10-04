package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Long> {
    Optional<CityEntity> findByName(String name);
    Optional<CityEntity> findByIataCode(String iataCode);
    List<CityEntity> findByCountryName(String countryName);
}
