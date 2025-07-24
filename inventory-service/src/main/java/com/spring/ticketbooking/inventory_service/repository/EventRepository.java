package com.spring.ticketbooking.inventory_service.repository;

import com.spring.ticketbooking.inventory_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
