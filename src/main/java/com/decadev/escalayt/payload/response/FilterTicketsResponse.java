package com.decadev.escalayt.payload.response;


import com.decadev.escalayt.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterTicketsResponse {
    private String message;
    private List<TicketDTO> tickets;
}


