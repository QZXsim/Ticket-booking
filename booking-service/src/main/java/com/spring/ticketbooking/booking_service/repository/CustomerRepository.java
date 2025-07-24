package com.spring.ticketbooking.booking_service.repository;

import com.spring.ticketbooking.booking_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {


}
