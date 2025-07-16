package com.plot.dto.mapper;
import com.plot.dto.NotificationDto;
import com.plot.dto.UserSummaryDto;
import com.plot.models.Notification;
import com.plot.models.User;
import com.plot.util.UserUtil;

public class NotificationDtoMapper {
	public static NotificationDto toNotificationDto(Notification noti) {
		NotificationDto dto = new NotificationDto();
		dto.setId(noti.getId());
		dto.setMessage(noti.getMessage());
		dto.setType(noti.getType());
		dto.setRead(noti.isRead());
		dto.setCreatedAt(noti.getCreatedAt());

		// Map sender/receiver User IDs directly, not full User objects
		//        dto.setSenderId(noti.getSender() != null ? noti.getSender().getId() : null);
		User senderUser = noti.getSender();

		if(senderUser != null) {
			dto.setSenderUser(UserUtil.changeUserSummary(senderUser));
		}else {
			dto.setSenderUser(null);
		}


		//        dto.setReceiverId(noti.getReceiver() != null ? noti.getReceiver().getId() : null);

		User receiverUser = noti.getReceiver();

		if(receiverUser != null) {

			dto.setReceiverUser(UserUtil.changeUserSummary(receiverUser));
		}else {
			dto.setReceiverUser(null);
		}


		return dto;
	}



}