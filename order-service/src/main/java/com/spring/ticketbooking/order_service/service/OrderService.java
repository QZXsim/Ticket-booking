package com.spring.ticketbooking.order_service.service;

import com.spring.ticketbooking.booking_service.event.BookingEvent;
import com.spring.ticketbooking.order_service.client.InventoryServiceClient;
import com.spring.ticketbooking.order_service.entity.Order;
import com.spring.ticketbooking.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @KafkaListener(topics = "booking", groupId = "order-booking-group")
    public void orderEvent(BookingEvent bookingEvent) {
        log.info("Received order booking event: {}", bookingEvent);

        // Create order object for DB
        Order order = createOrder(bookingEvent);

        // Save order to DB
        orderRepository.saveAndFlush(order);
        log.info("Order saved successfully: {}", order);

        // Update Inventory Service
        inventoryServiceClient.updateInventory(order.getEventId(), order.getTicketCount());
        log.info("Inventory updated for event ID: {}, ticket count: {}", order.getEventId(), order.getTicketCount());
    }

    private Order createOrder(BookingEvent bookingEvent) {
        return Order.builder()
                .customerId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }
}
