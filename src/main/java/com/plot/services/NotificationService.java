package com.plot.services;

import java.util.List;

import com.plot.dto.NotificationDto;
import com.plot.enums.NotificationType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.UserException;
import com.plot.models.Notification;
import com.plot.models.User;

public interface NotificationService {
	
	void createNewNotification(NotificationDto notificationDto) throws UserException,NotificationException;

    List<Notification> getUserNotifications(User user) throws UserException,NotificationException;

    void markAllAsRead(User user) throws UserException,NotificationException;

    void deleteNotification(Long notificationId, User user) throws UserException,NotificationException;

}
