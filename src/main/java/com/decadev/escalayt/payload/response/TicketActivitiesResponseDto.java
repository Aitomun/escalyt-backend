package com.decadev.escalayt.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketActivitiesResponseDto {
    private Long ticketNumber;
    private String title;
    private String priority;
    private String assignee;
    private String status;
    private String category;
    private LocalDateTime createdDate;
    private String location;

    public TicketActivitiesResponseDto(Long id, String title, String string, String string1, String categoryName, LocalDateTime createdDate, String location) {
    }
}