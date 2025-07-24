package com.spring.ticketbooking.inventory_service.response;

import com.spring.ticketbooking.inventory_service.entity.Venue;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventInventoryResponse {

    private Long eventId;
    private String event;
    private Long capacity;
    private Venue venue;
    private BigDecimal ticketPrice;

}
