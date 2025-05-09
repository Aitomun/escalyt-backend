package com.decadev.escalayt.payload.response;

import com.decadev.escalayt.enums.Priority;
import com.decadev.escalayt.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketDTO {

    private Long id;
    private Long createdBy;
    private String title;
    private Priority priority;
    private Status status;
    private String location;
    private String description;
    private String attachment;
    private Long orgId;
    private String assigneeId;
    private String categoryId;
    private List<CommentResponse> comments;
    private List<RateResponse> rates;
    private LocalDateTime createdDate;
    private boolean resolved;
}

