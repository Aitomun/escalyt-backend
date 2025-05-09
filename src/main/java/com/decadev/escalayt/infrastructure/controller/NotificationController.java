package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.entity.Notification;
import com.decadev.escalayt.payload.request.NotificationRequest;
import com.decadev.escalayt.payload.response.NotificationResponse;
import com.decadev.escalayt.repository.NotificationRepository;
import com.decadev.escalayt.service.NotificationService;
import com.decadev.escalayt.service.impl.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @PostMapping("/send-to-all")
    public ResponseEntity<NotificationResponse> sendNotificationToAll(@RequestBody NotificationRequest request) {
        try {
            notificationService.sendNotificationToAll(request);
            return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "Notification has been sent to all tokens."), HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            NotificationResponse response = NotificationResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to send notification.")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity sendNotificationToUser(@PathVariable Long userId, @RequestBody NotificationRequest request) throws ExecutionException, InterruptedException{
        notificationService.sendNotificationToUser(userId,request);
        return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    @GetMapping("/{userId}/all")
    public List<Notification> getAllNotificationsByUserId(Long userId){
        return notificationRepository.findByPersonId(userId);
    }
}
