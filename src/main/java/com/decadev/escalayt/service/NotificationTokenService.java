package com.decadev.escalayt.service;

public interface NotificationTokenService {
    void saveToken(Long userId, String token);
    void deleteToken(String token);
}