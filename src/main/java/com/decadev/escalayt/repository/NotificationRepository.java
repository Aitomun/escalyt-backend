package com.decadev.escalayt.repository;

import com.decadev.escalayt.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPersonId(Long personId);
}
