package com.plot.sevices.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plot.config.JwtProvider;
import com.plot.dto.NotificationDto;
import com.plot.dto.UserDto;
import com.plot.enums.NotificationType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.UserException;
import com.plot.models.Notification;
import com.plot.models.User;
import com.plot.repositories.NotificationRepository;
import com.plot.repositories.UserRepository;
import com.plot.services.NotificationService;
import com.plot.services.UserService;
import com.plot.util.UserUtil;

@Service
public class UserServiceImpl implements UserService {
	

	@Autowired
	private UserRepository	userRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired 
	private JwtProvider jwtProvider;


	@Override
	public User findUserById(Long userId) throws UserException {
		// TODO Auto-generated method stub
		User user = userRepository.findById(userId)
				.orElseThrow(()->new UserException("user not found with id" + userId));
		return user;
	}


	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		// TODO Auto-generated method stub
		String email = jwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email);
		if(user == null) {
			throw new UserException("user not found with email "+ email);
		}
		return user;
	}


	@Override
	public User updateUser(Long userId, User updateUser) throws UserException {
	    User user = findUserById(userId);

	    user.setFullName(updateUser.getFullName());
	    user.setImage(updateUser.getImage());
	    user.setBackgroundImage(updateUser.getBackgroundImage());
	    user.setBirthDate(updateUser.getBirthDate());
	    user.setLocation(updateUser.getLocation());
	    user.setBio(updateUser.getBio());
	    user.setPhone(updateUser.getPhone());
	    user.setWork(updateUser.getWork());
	    user.setEducation(updateUser.getEducation());

	    return userRepository.save(user);
	}


	@Override
	public User followUser(Long userId, User user) throws UserException {
		// TODO Auto-generated method stub
		  // Ma Ma = the user to be followed or unfollowed
	    User followToUser = findUserById(userId); // Ma Ma

	    if (userId.equals(user.getId())) {
	        throw new UserException("You cannot follow yourself.");
	    }

	    // Check if already following
	    if (followToUser.getFollowers().contains(user)) {
	        // Unfollow
	        followToUser.getFollowers().remove(user);    // Remove Mg Mg from Ma Ma's followers
	        user.getFollowings().remove(followToUser);    // Remove Ma Ma from Mg Mg's following
	    } else {
	        // Follow
	        followToUser.getFollowers().add(user);       // Add Mg Mg to Ma Ma's followers
	        user.getFollowings().add(followToUser);       // Add Ma Ma to Mg Mg's following
	        
	        NotificationDto newNoti = new NotificationDto();
	        
	        newNoti.setSenderUser(UserUtil.changeUserSummary(user));
	        newNoti.setReceiverUser(UserUtil.changeUserSummary(followToUser));
	        newNoti.setMessage(user.getFullName()+ " follow you");
	        newNoti.setType(NotificationType.FOLLOW);
	        
	        try {
				notificationService.createNewNotification(newNoti);
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	    }

	    userRepository.save(followToUser);
	    userRepository.save(user);
	    return followToUser;
	}


	@Override
	public List<User> searchUser(String query) {
		// TODO Auto-generated method stub
		return userRepository.searchUser(query);
	}


	@Override
	 @Transactional(readOnly = true)
	public User findByIdWithFollowers(Long userId) throws UserException {
		// TODO Auto-generated method stub
		 return userRepository.findByIdWithFollowers(userId)
                 .orElseThrow(() -> new UserException("User not found with id: " + userId));
	}


	@Override
	public List<User> getAllUser() throws UserException {
		// TODO Auto-generated method stub
		
		  List<User> users = userRepository.findAll(); 
		  
		  return users;  
	}
	
	

}
