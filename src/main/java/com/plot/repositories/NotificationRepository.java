package com.plot.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plot.models.Notification;
import com.plot.models.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(User user);
    
    List<Notification>findByReceiverAndIsReadFalse(User user);
}
