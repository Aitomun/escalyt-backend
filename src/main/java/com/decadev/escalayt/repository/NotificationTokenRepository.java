package com.decadev.escalayt.repository;


import com.decadev.escalayt.entity.NotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken,Long> {
    void deleteByToken(String token);
    NotificationToken findByToken(String token);
}
