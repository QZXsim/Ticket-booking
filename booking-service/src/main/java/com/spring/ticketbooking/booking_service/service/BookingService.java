package com.spring.ticketbooking.booking_service.service;

import com.spring.ticketbooking.booking_service.client.InventoryServiceClient;
import com.spring.ticketbooking.booking_service.entity.Customer;
import com.spring.ticketbooking.booking_service.event.BookingEvent;
import com.spring.ticketbooking.booking_service.repository.CustomerRepository;
import com.spring.ticketbooking.booking_service.request.BookingRequest;
import com.spring.ticketbooking.booking_service.response.BookingResponse;
import com.spring.ticketbooking.booking_service.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Autowired
    public BookingService(final CustomerRepository customerRepository,
                          final InventoryServiceClient inventoryServiceClient,
                          final KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }


    public BookingResponse createBooking(final BookingRequest bookingRequest) {
        // check if user exists
        final Customer customer = customerRepository.findById(bookingRequest.getUserId()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("User not found");
        }

        // check if there is enough inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(bookingRequest.getEventId());
        log.info("Inventory response received: {}", inventoryResponse);
        if (inventoryResponse.getCapacity() < bookingRequest.getTicketCount()) {
            throw new RuntimeException("Not enough tickets available");
        }
        // create booking
        final BookingEvent bookingEvent = createBookingEvent(bookingRequest, customer, inventoryResponse);

        // send booking to order-service on a Kafka topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking event sent to Kafka: {}", bookingEvent);
        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingRequest.getEventId())
                .ticketCount(bookingRequest.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();

    }

    private BookingEvent createBookingEvent(final BookingRequest bookingRequest,
                                            final Customer customer,
                                            final InventoryResponse inventoryResponse) {
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(bookingRequest.getEventId())
                .ticketCount(bookingRequest.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(bookingRequest.getTicketCount())))
                .build();
    }

}
