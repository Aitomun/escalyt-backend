package com.decadev.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RateResponse {


    private Long id;
    private Long ticketId;
    private Integer ratingCount;
    private String reviewMessage;
    private Long personId;
}
