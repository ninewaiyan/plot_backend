package com.plot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.plot.dto.LikeDto;
import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.Like;
import com.plot.models.User;
import com.plot.services.LikeService;
import com.plot.services.UserService;
import com.plot.dto.mapper.LikeDtoMapper;


@RestController
@RequestMapping("/api")
public class LikeController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LikeService likeService;
	
	@PostMapping("/{plotId}/like")
	public ResponseEntity<LikeDto>likePlot(@PathVariable Long plotId,
			@RequestHeader("Authorization")String jwt)throws UserException,PlotException, WalletException{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		Like like = likeService.likePlot(plotId,user);
		
		LikeDto likeDto = LikeDtoMapper.toLikeDto(like,user);
		
		return new ResponseEntity<LikeDto>(likeDto,HttpStatus.CREATED);	
			
	}

}
