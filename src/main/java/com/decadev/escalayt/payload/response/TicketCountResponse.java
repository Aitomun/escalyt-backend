package com.decadev.escalayt.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCountResponse {

    private Long open;
    private Long inReview;
    private Long resolved;

}
