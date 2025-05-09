package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllTicketsResponse {

    private Long id;

    private String title;

    private Status status;

    private Priority priority;

    private String category;

    private String assignee;

    private LocalDateTime createdDate;

    private  String location;
}
