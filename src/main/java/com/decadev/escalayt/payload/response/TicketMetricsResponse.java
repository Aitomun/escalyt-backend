package com.decadev.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TicketMetricsResponse {
    private  long totalTickets;
    private Map<String, Long> ticketStatusCount;
    private double averageResolutionTimeInMinutes;
}
