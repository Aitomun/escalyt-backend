package com.decadev.escalayt.infrastructure.controller;


import com.decadev.escalayt.payload.response.TicketMetricsResponse;
import com.decadev.escalayt.service.impl.TicketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class TicketMetricController {
    private final TicketServiceImpl ticketService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ticket-metrics")
    public ResponseEntity<TicketMetricsResponse> getTicketMetrics() {
        long total = ticketService.countAll();
        var statusMap = ticketService.countByStatus();
        double avgTime = ticketService.averageResolutionTime();

        TicketMetricsResponse metrics = TicketMetricsResponse.builder()
                .totalTickets(total)
                .ticketStatusCount(statusMap)
                .averageResolutionTimeInMinutes(avgTime)
                .build();

        return ResponseEntity.ok(metrics);
    }

}
