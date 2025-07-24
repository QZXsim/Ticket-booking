package com.spring.ticketbooking.inventory_service.repository;

import com.spring.ticketbooking.inventory_service.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

}
