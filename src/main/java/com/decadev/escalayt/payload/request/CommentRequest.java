package com.decadev.escalayt.payload.request;

import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.entity.Ticket;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    @NotEmpty(message = "Message cannot be empty")

    private String message;

}