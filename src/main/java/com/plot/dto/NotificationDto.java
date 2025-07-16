package com.plot.dto;

import java.time.LocalDateTime;

import com.plot.enums.NotificationType;
import com.plot.models.User;

import lombok.Data;

@Data
public class NotificationDto {
	private Long id;

	//    private Long senderId; // Use Long for ID
	//    private Long receiverId; // Use Long for ID

	private UserSummaryDto senderUser;
	private UserSummaryDto receiverUser;

	private String message;

	private NotificationType type;

	private boolean isRead;
	private LocalDateTime createdAt;
}
