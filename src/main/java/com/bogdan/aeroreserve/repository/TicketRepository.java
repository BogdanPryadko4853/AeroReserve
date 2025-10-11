package com.bogdan.aeroreserve.repository;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.bogdan.aeroreserve.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    Optional<TicketEntity> findByBooking(BookingEntity booking);
    Optional<TicketEntity> findByTicketNumber(String ticketNumber);
}