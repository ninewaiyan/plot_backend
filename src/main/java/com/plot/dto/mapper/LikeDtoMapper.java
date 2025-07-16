package com.plot.dto.mapper;

import com.plot.dto.LikeDto;
import com.plot.models.Like;
import com.plot.models.User;
import com.plot.dto.UserDto;
import com.plot.dto.PlotDto;
import java.util.List;
import java.util.ArrayList;
public class LikeDtoMapper {

	public static LikeDto toLikeDto(Like like,User reqUser) {

		UserDto user = UserDtoMapper.toUserDto(like.getUser());
		UserDto reqUserDto = UserDtoMapper.toUserDto(reqUser);
		PlotDto plot = PlotDtoMapper.toPlotDto(like.getPlot(), reqUser);

		LikeDto likeDto = new LikeDto();
		likeDto.setId(like.getId());
		likeDto.setPlot(plot);
		likeDto.setUser(user);

		return likeDto;

	}

	public static List<LikeDto>toLikeDtos(List<Like>likes, User reqUser){
		List<LikeDto>likeDtos = new ArrayList<>();

		for(Like like:likes) {
			UserDto user = UserDtoMapper.toUserDto(like.getUser());
			PlotDto plot = PlotDtoMapper.toPlotDto(like.getPlot(), reqUser);

			LikeDto likeDto = new LikeDto();
			likeDto.setId(like.getId());
			likeDto.setPlot(plot);
			likeDto.setUser(user);

			likeDtos.add(likeDto);

		}
		return likeDtos;

	}


}
