package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.payload.request.EmailDetails;
import com.decadev.escalayt.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlerts(EmailDetails emailDetails, String templateName) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        Map<String, Object> variables = Map.of(
                "name", emailDetails.getFirstName() + " " + emailDetails.getLastName(),
                "link", emailDetails.getLink()

        );
        context.setVariables(variables);

        messageHelper.setFrom(senderEmail);
        messageHelper.setTo(emailDetails.getRecipient());
        messageHelper.setSubject(emailDetails.getSubject());

        String html = templateEngine.process(templateName, context);
        messageHelper.setText(html, true);

        javaMailSender.send(message);
        log.info("Sending email: to {}", emailDetails.getRecipient());

    }

    @Override
    public void sendNewEmployeeAlerts(EmailDetails emailDetails) throws MessagingException {

        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setText(emailDetails.getMessageBody());
            simpleMailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(simpleMailMessage);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}
