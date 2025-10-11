package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}
