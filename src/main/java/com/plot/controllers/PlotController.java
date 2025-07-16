package com.plot.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plot.dto.NotificationDto;
import com.plot.dto.PlotDto;
import com.plot.dto.UserDto;
import com.plot.dto.mapper.PlotDtoMapper;
import com.plot.dto.mapper.UserDtoMapper;
import com.plot.enums.NotificationType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.models.Plot;
import com.plot.models.User;
import com.plot.repositories.UserRepository;
import com.plot.requests.ReplotRequest;
import com.plot.services.NotificationService;
import com.plot.services.PlotService;
import com.plot.services.UserService;
import com.plot.util.UserUtil;

@RestController
@RequestMapping("api/plots")
public class PlotController {

	@Autowired
	private PlotService  plotService;

	@Autowired
	private UserService userService;

	@Autowired
	private NotificationService notificationService;




	@PostMapping("/create")
	public ResponseEntity<PlotDto>createPlot(@RequestBody Plot reqPlot,
			@RequestHeader("Authorization")String jwt)throws UserException ,PlotException{

		User user = userService.findUserProfileByJwt(jwt);

		Plot plot = plotService.createPlot(reqPlot,user);

		PlotDto plotDto = PlotDtoMapper.toPlotDto(plot,user);

		UserDto userDto = UserDtoMapper.toUserDto(user);


		List<UserDto> followers  = userDto.getFollowers();

		System.out.println("xxxxxxxxxxxxxxxxxxxxxxxx Followers =>" + followers.size());

		if(!followers.isEmpty()) {
			for(UserDto follower : followers) {

				if(follower.getId() != null && !follower.getId().equals(user.getId())) {
					NotificationDto newNoti = new NotificationDto();
					newNoti.setSenderUser(UserUtil.changeUserSummary(user));
					newNoti.setReceiverUser(UserUtil.changeUserDtoToSummary(follower));
					newNoti.setType(NotificationType.CREATE_PLOT);
					newNoti.setRead(false);
					newNoti.setMessage("create a plot.");
					newNoti.setCreatedAt(LocalDateTime.now());
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

			}
		}

		return new ResponseEntity<>(plotDto,HttpStatus.CREATED);

	}


	@GetMapping("/")
	public ResponseEntity<List<PlotDto>>getAllPlot(
			@RequestHeader("Authorization")String jwt)throws UserException,PlotException{

		User user = userService.findUserProfileByJwt(jwt);

		List<Plot>plots = plotService.findAllPlots();

		List<PlotDto>plotDtos = PlotDtoMapper.toPlotDtos(plots,user);


		return new ResponseEntity<>(plotDtos,HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PlotDto> getPlotById(
			@RequestHeader("Authorization") String jwt,
			@PathVariable Long id) throws UserException, PlotException {

		User user = userService.findUserProfileByJwt(jwt);

		Plot plot = plotService.findById(id); // You should already have this method

		PlotDto plotDto = PlotDtoMapper.toPlotDto(plot, user);

		return new ResponseEntity<>(plotDto, HttpStatus.OK);
	}


	@PutMapping("/update/{plotId}")
	public ResponseEntity<PlotDto> updatePlot(
			@PathVariable Long plotId,
			@RequestBody Plot reqPlot,
			@RequestHeader("Authorization") String jwt
			) throws UserException, PlotException {

		User user = userService.findUserProfileByJwt(jwt);

		Plot existingPlot = plotService.findById(plotId); // You must create this method in service

		// 🔒 Check ownership
		if (!existingPlot.getUser().getId().equals(user.getId())) {
			throw new PlotException("You are not authorized to update this plot.");
		}

		Plot updatedPlot = plotService.updatePlot(plotId, reqPlot);

		PlotDto plotDto = PlotDtoMapper.toPlotDto(updatedPlot, user);

		List<User> followers  = user.getFollowers();

		if(!followers.isEmpty()) {
			for(User follower : followers) {

				if(follower.getId() != null && !follower.getId().equals(user.getId())) {
					NotificationDto newNoti = new NotificationDto();
					newNoti.setSenderUser(UserUtil.changeUserSummary(user));
					newNoti.setReceiverUser(UserUtil.changeUserSummary(follower));
					newNoti.setType(NotificationType.UPDATE_PLOT);
					newNoti.setRead(false);
					newNoti.setMessage( "update  plot.");
					newNoti.setCreatedAt(LocalDateTime.now());
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

			}
		}

		return new ResponseEntity<>(plotDto, HttpStatus.OK);
	}

	@PutMapping("/{plotId}/view")
	public ResponseEntity<PlotDto> viewPlot(
			@PathVariable Long plotId,
			@RequestHeader("Authorization") String jwt
			) throws UserException, PlotException {

		User user = userService.findUserProfileByJwt(jwt);

		Plot existingPlot = plotService.findById(plotId); // You must create this method in service

		// 🔒 Check ownership
		if (!existingPlot.getUser().getId().equals(user.getId())) {

			Plot updatedPlot = plotService.viewPlot(plotId);

			PlotDto plotDto = PlotDtoMapper.toPlotDto(updatedPlot, user);

			return new ResponseEntity<>(plotDto, HttpStatus.OK);
		}else {

			PlotDto plotDto = PlotDtoMapper.toPlotDto(existingPlot, user);
			return new ResponseEntity<>(plotDto, HttpStatus.OK);	
		}

	}


	@DeleteMapping("/delete/{plotId}")
	public ResponseEntity<String> deletePlot(
			@PathVariable Long plotId,
			@RequestHeader("Authorization") String jwt
			) throws UserException, PlotException {

		User user = userService.findUserProfileByJwt(jwt);

		Plot plot = plotService.findById(plotId); // reuse the findById method

		// 🔒 Check ownership
		if (!plot.getUser().getId().equals(user.getId())) {
			throw new PlotException("You are not authorized to delete this plot.");
		}

		plotService.deletePlotById(plotId,user.getId()); // implement this in your service

		return new ResponseEntity<>("Plot deleted successfully", HttpStatus.OK);
	}

	@PutMapping("/{plotId}/collect")
	public ResponseEntity<PlotDto>collect(@PathVariable Long plotId,
			@RequestHeader("Authorization")String jwt)throws UserException,PlotException{

		User user = userService.findUserProfileByJwt(jwt);

		Plot plot = plotService.collect(plotId, user);

		PlotDto plotDto = PlotDtoMapper.toPlotDto(plot,user);

		return new ResponseEntity<>(plotDto,HttpStatus.OK);

	}

	@PostMapping("/replot")
	public ResponseEntity<PlotDto> replot(
			@RequestBody ReplotRequest replotRequest,
			@RequestHeader("Authorization") String jwt
			) throws UserException, PlotException {

		// Get the current user (who is reploting)
		User currentUser = userService.findUserProfileByJwt(jwt);

		// Get the original plot by ID
		Plot originalPlot = plotService.findById(replotRequest.getPlotId());

		// Get the original author of the plot
		User originalAuthor = originalPlot.getUser();

		// Create a replot (you can pass the originalAuthor if needed)
		Plot newReplot = plotService.createReplot(replotRequest, currentUser);

		if(newReplot != null) {
			NotificationDto newNoti = new NotificationDto();
			newNoti.setSenderUser(UserUtil.changeUserSummary(currentUser));
			newNoti.setReceiverUser(UserUtil.changeUserSummary(currentUser));
			newNoti.setType(NotificationType.REPLOT);
			newNoti.setRead(false);
			newNoti.setMessage(currentUser.getFullName() +" replot you.");
			newNoti.setCreatedAt(LocalDateTime.now());
		}
		// Convert to DTO (include current user context)
		PlotDto plotDto = PlotDtoMapper.toPlotDto(newReplot, currentUser);

		return new ResponseEntity<>(plotDto, HttpStatus.CREATED);
	}

	
	@GetMapping("/user/{userId}/likes")
	public ResponseEntity<List<PlotDto>>findPlotByLikesContainUser(@PathVariable Long userId,
			@RequestHeader("Authorization")String jwt)throws UserException,PlotException{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		List<Plot>plots = plotService.findByLikesContainsUser(user);
	
		List<PlotDto>plotDtos = PlotDtoMapper.toPlotDtos(plots,user);
		
		return new ResponseEntity<>(plotDtos,HttpStatus.OK);
	}
	
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PlotDto>> getUserPlots(@PathVariable Long userId) throws UserException {
	    User user = userService.findUserById(userId); // Already throws if not found
	    List<Plot> plots = plotService.getUserPlots(user);
		List<PlotDto>plotDtos = PlotDtoMapper.toPlotDtos(plots,user);
	    return ResponseEntity.ok(plotDtos);
	}

}
