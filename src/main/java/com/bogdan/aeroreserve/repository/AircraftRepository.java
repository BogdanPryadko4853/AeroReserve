package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.AircraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<AircraftEntity, Long> {
    Optional<AircraftEntity> findByModel(String model);
    boolean existsByModel(String model);
}