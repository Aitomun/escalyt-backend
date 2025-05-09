package com.decadev.escalayt.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterTicketsRequest {
    private String priority;
    private String status;
    private String category;
    private Long assigneeId;
}




