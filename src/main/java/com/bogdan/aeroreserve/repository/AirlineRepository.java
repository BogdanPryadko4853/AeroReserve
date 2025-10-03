package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.AirlineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<AirlineEntity, Long> {
    Optional<AirlineEntity> findByName(String name);
    Optional<AirlineEntity> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
