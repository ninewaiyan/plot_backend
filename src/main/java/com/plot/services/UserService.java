package com.plot.services;

import java.util.List;

import com.plot.dto.UserDto;
import com.plot.exceptions.UserException;
import com.plot.models.User;

public interface UserService {
	
   public User findUserById(Long userId)throws UserException;
   
   public User findByIdWithFollowers(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User updateUser(Long userId,User upateUser)throws UserException;
	
	public User followUser(Long userId,User user)throws UserException;
	
	public List<User>searchUser(String query);
	
	public List<User>getAllUser()throws UserException;
	
	

}
