package com.plot.sevices.Impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plot.dto.NotificationDto;
import com.plot.enums.NotificationType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.UserException;
import com.plot.models.Notification;
import com.plot.models.User;
import com.plot.repositories.NotificationRepository;
import com.plot.repositories.UserRepository;
import com.plot.services.NotificationService;

import jakarta.transaction.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

   

    @Override
    public List<Notification> getUserNotifications(User user)
            throws UserException, NotificationException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }

        return notificationRepository.findByReceiver_IdOrderByCreatedAtDesc(user);
    }

   

    @Override
    @Transactional
    public void markAllAsRead(User user) throws UserException, NotificationException {
        if (user == null) {
            throw new UserException("User cannot be null");
        }

        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadFalse(user);

        if (notifications == null || notifications.isEmpty()) {
            return; // no unread notifications, nothing to do
        }

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        try {
            notificationRepository.saveAll(notifications);
        } catch (Exception e) {
            throw new NotificationException("Failed to mark notifications as read");
        }
    }


    @Override
    public void createNewNotification(NotificationDto notificationDto) throws UserException, NotificationException {
        Notification newNoti = new Notification();

        // Set sender if provided (null for system notifications)
        if (notificationDto.getSenderUser() != null ) {
            User sender = userRepository.findById(notificationDto.getSenderUser().getId())
                .orElseThrow(() -> new UserException("Sender not found with ID: " + notificationDto.getSenderUser().getId()));
            newNoti.setSender(sender);
        } else {
            newNoti.setSender(null); // System-generated notification
        }

        // Validate and set receiver
        if (notificationDto.getReceiverUser() != null) {
        	 User receiver = userRepository.findById(notificationDto.getReceiverUser().getId())
        	            .orElseThrow(() -> new UserException("Receiver not found with ID: " + notificationDto.getReceiverUser().getId()));

        	        newNoti.setReceiver(receiver);
        }else {
        	newNoti.setReceiver(null);
        }

       
        newNoti.setType(notificationDto.getType());
        newNoti.setMessage(notificationDto.getMessage());

        notificationRepository.save(newNoti);
    }

    @Override
    public void deleteNotification(Long notificationId, User user) throws UserException, NotificationException {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationException("Notification not found with ID: " + notificationId));

        // Authorization check: ensure the user is the receiver
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new UserException("You are not authorized to delete this notification.");
        }

        // Remove notification from user's notification list
        user.getNotifications().remove(notification);

        // Also remove reference from the notification if bidirectional
        notification.setReceiver(null);

        // Save user to persist the change in the relationship
        userRepository.save(user);

        // Finally delete the notification itself
        notificationRepository.delete(notification);
    }
	
}
