package com.plot.util;

import com.plot.models.Like;
import com.plot.models.Plot;
import com.plot.models.User;

public class PlotUtil {

	public final static boolean isLikedByReqUser(User reqUser , Plot plot) {

		for(Like like:plot.getLikes()) {
			if(like.getUser().getId().equals(reqUser.getId())) {
				return true;
			}
		}

		return false;

	}
	
	public final static boolean isCollectedByReqUser(User reqUser,Plot plot) {

		for(User user:plot.getCollectedUsers()) {

			if(user.getId().equals(reqUser.getId())) {
				return true;
			}

		}
		return false;
	}

}
