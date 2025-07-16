package com.plot.util;

import com.plot.dto.PlotUserDto;
import com.plot.dto.UserDto;
import com.plot.dto.UserSummaryDto;
import com.plot.dto.WalletBasicDto;
import com.plot.models.User;

public class UserUtil {
	
 public static UserSummaryDto changeUserSummary(User user) {

		UserSummaryDto summaryUser = new UserSummaryDto();
		summaryUser.setId(user.getId());
		summaryUser.setFullName(user.getFullName());
		summaryUser.setEmail(user.getEmail());
		summaryUser.setImage(user.getImage());
		return   summaryUser;

	}
 
 public static UserSummaryDto changeUserDtoToSummary(UserDto user) {

		UserSummaryDto summaryUser = new UserSummaryDto();
		summaryUser.setId(user.getId());
		summaryUser.setFullName(user.getFullName());
		summaryUser.setEmail(user.getEmail());
		summaryUser.setImage(user.getImage());
		return   summaryUser;

	}
 public static PlotUserDto changeUserPlotUser(UserDto user) {

		PlotUserDto plotUserDto  = new PlotUserDto();
		plotUserDto.setId(user.getId());
		plotUserDto.setFullName(user.getFullName());
		plotUserDto.setEmail(user.getEmail());
		plotUserDto.setImage(user.getImage());
		WalletBasicDto walletBasicDto = new WalletBasicDto(); 
		walletBasicDto.setId(user.getWallet().getId());
		plotUserDto.setWallet(walletBasicDto);
		
		return  plotUserDto;


	}
 
 
 
	public static final boolean isReqUser(User reqUser, User user2) {
		return reqUser.getId().equals(user2.getId());
	}
	
	public static final boolean isFollowedByReqUser(User reqUser,User user2) {
     	return user2.getFollowers().contains(reqUser);
		
	}

}
