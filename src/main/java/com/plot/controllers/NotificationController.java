package com.plot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plot.dto.LikeDto;
import com.plot.dto.NotificationDto;
import com.plot.dto.mapper.LikeDtoMapper;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.Like;
import com.plot.models.User;
import com.plot.services.NotificationService;
import com.plot.services.UserService;

@RestController
@RequestMapping("/api")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private UserService userService;
	
	
	@PutMapping("/noti/read")
	public ResponseEntity<?>readNoti(
			@RequestHeader("Authorization")String jwt)throws UserException,PlotException, WalletException, NotificationException{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		notificationService.markAllAsRead(user);
        return ResponseEntity.ok("All notifications marked as read");
	}
	
	@DeleteMapping("/noti/{id}")
	public ResponseEntity<?> deleteNotification(
	        @PathVariable Long id,
	        @RequestHeader("Authorization") String jwt
	) throws UserException, PlotException, WalletException, NotificationException {

	    User user = userService.findUserProfileByJwt(jwt);

	    notificationService.deleteNotification(id, user);

	    return ResponseEntity.ok("Notification deleted successfully");
	}
	
	
}
