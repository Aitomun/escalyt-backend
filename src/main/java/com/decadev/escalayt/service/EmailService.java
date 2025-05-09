package com.decadev.escalayt.service;

import com.decadev.escalayt.payload.request.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailAlerts(EmailDetails emailDetails, String templateName) throws MessagingException;

    void sendNewEmployeeAlerts(EmailDetails emailDetails) throws MessagingException;
}
