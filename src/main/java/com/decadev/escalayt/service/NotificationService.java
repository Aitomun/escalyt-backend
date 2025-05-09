package com.decadev.escalayt.service;

import com.decadev.escalayt.payload.request.NotificationRequest;

import java.util.concurrent.ExecutionException;

public interface NotificationService {
    void sendNotificationToAll(NotificationRequest request) throws ExecutionException, InterruptedException;
    void sendNotificationToUser(Long userId, NotificationRequest request) throws ExecutionException, InterruptedException;
}
