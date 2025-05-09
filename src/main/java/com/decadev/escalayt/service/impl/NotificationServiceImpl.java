package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.Notification;
import com.decadev.escalayt.entity.NotificationToken;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.exceptions.NotificationException;
import com.decadev.escalayt.payload.request.NotificationRequest;
import com.decadev.escalayt.repository.NotificationRepository;
import com.decadev.escalayt.repository.NotificationTokenRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationTokenRepository tokenRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private PersonRepository personRepository;

    public void sendNotificationToAll(NotificationRequest request) throws ExecutionException, InterruptedException {
        List<NotificationToken> tokens = tokenRepository.findAll();
        for (NotificationToken token : tokens) {
            request.setToken(token.getToken());
            fcmService.sendMessageToToken(request);
        }
    }
    public void sendNotificationToUser(Long userId, NotificationRequest request) {
        Person user = personRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<NotificationToken> tokens = user.getNotificationTokens();
        for (NotificationToken token : tokens) {
            request.setToken(token.getToken());
            try{
                fcmService.sendMessageToToken(request);
            }catch (ExecutionException | InterruptedException e) {
                throw new NotificationException("Failed to send notification", e);
            }

        }
        Notification newNotification = Notification.builder()
                .topic(request.getTopic())
                .body(request.getBody())
                .title(request.getTitle())
                .person(user)
                .build();
        notificationRepository.save(newNotification);
    }

}
