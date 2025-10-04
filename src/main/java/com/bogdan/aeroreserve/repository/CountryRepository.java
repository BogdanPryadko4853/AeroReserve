package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByCode(String code);
    Optional<CountryEntity> findByName(String name);
}
