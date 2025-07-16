package com.plot.services;

import java.util.List;

import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.Like;
import com.plot.models.User;

public interface LikeService {

	public Like likePlot(Long plotId,User user)throws UserException,PlotException,WalletException;
	
	public List<Like>getAllLikes(Long plotId)throws PlotException;
}
