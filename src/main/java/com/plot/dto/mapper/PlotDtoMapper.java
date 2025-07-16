package com.plot.dto.mapper;


import java.util.ArrayList;
import java.util.List;

import com.plot.dto.PlotDto;
import com.plot.dto.UserDto;
import com.plot.models.Plot;
import com.plot.models.User;
import com.plot.util.PlotUtil;
import com.plot.util.UserUtil;

public class PlotDtoMapper {
	
	public static PlotDto toPlotDto(Plot plot ,User reqUser) {
		
		UserDto user = UserDtoMapper.toUserDto(plot.getUser());
		
		boolean isLiked = PlotUtil.isLikedByReqUser(reqUser, plot);
		
		boolean isCollected = PlotUtil.isCollectedByReqUser(reqUser, plot);
		
		List<Long>collectedUserId = new ArrayList<>();
		
		for(User user1 : plot.getCollectedUsers()) {
			collectedUserId.add(user1.getId());
		}
		
		PlotDto  plotDto = new PlotDto();
		plotDto.setId(plot.getId());
		plotDto.setContent(plot.getContent());
		plotDto.setMedia(plot.getMedia());
		plotDto.setCreatedAt(plot.getCreatedAt());
		plotDto.setUpdatedAt(plot.getUpdatedAt());
		plotDto.setTotalLikes(plot.getLikes().size());
		plotDto.setTotalReplot(plot.getRePlots().size());
		plotDto.setTotalCollects(plot.getCollectedUsers().size());
		plotDto.setUser(UserUtil.changeUserPlotUser(user));
		plotDto.setLiked(isLiked);
		plotDto.setCollected(isCollected);
		plotDto.setCollectUserIds(collectedUserId);
		plotDto.setViews(plot.getViews());
		plotDto.setRePlots(toPlotDtos(plot.getRePlots(),reqUser));
		
		return plotDto;

	}
	
	public static List<PlotDto> toPlotDtos(List<Plot> plots, User reqUser) {

		List<PlotDto> plotDtos = new ArrayList<>();
		for (Plot plot : plots) {
			PlotDto plotDto = toRePlotDto(plot, reqUser);
			plotDtos.add(plotDto);
		}

		return plotDtos;
	}

	private static PlotDto toRePlotDto(Plot plot, User reqUser) {
		UserDto user = UserDtoMapper.toUserDto(plot.getUser());

		boolean isLiked = PlotUtil.isLikedByReqUser(reqUser, plot);

		boolean isCollected = PlotUtil.isCollectedByReqUser(reqUser, plot);

		List<Long> collectedUserId = new ArrayList<>();

		for (User user1 : plot.getCollectedUsers()) {
			collectedUserId.add(user1.getId());
		}

		PlotDto plotDto = new PlotDto();
		plotDto.setId(plot.getId());
		plotDto.setContent(plot.getContent());
		plotDto.setCreatedAt(plot.getCreatedAt());
		plotDto.setUpdatedAt(plot.getUpdatedAt());
		plotDto.setMedia(plot.getMedia());
		plotDto.setTotalLikes(plot.getLikes().size());
		plotDto.setTotalReplot(plot.getRePlots().size());
		plotDto.setTotalCollects(plot.getCollectedUsers().size());
		plotDto.setUser(UserUtil.changeUserPlotUser(user));
		plotDto.setLiked(isLiked);
		plotDto.setCollected(isCollected);
		plotDto.setCollectUserIds(collectedUserId);
		plotDto.setViews(plot.getViews());
		return plotDto;
	}

}
