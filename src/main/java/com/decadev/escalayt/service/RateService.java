package com.decadev.escalayt.service;
import com.decadev.escalayt.payload.request.RateRequest;
import com.decadev.escalayt.payload.response.RateResponse;
import jakarta.mail.MessagingException;

public interface RateService {
    RateResponse createRating(Long ticketId, RateRequest rateRequest) throws MessagingException;
}
