package com.decadev.escalayt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TicketNotResolvedException extends RuntimeException {
    public TicketNotResolvedException(String message) {
        super(message);
    }
}