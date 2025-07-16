package com.plot.services;

import java.util.List;

import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.models.Plot;
import com.plot.models.User;
import com.plot.requests.ReplotRequest;

public interface PlotService {
	
	// Create a new Plot
    Plot createPlot(Plot reqPlot, User user) throws UserException;
    

    // Get all main Plots (not replies)
    List<Plot> findAllPlots();

    // User collects (like retweet) a Plot
    Plot collect(Long plotId, User user) throws UserException, PlotException;

    // Find a Plot by ID
    Plot findById(Long plotId) throws PlotException;

    // Delete a Plot
    void deletePlotById(Long plotId, Long userId) throws PlotException, UserException;

    // Remove a collected Plot
    Plot removeFromCollect(Long plotId, User user) throws PlotException, UserException;

    // Create a reply Plot
    Plot createReplot(ReplotRequest reqPlot, User user) throws PlotException;

    // Get all Plots by user
    List<Plot> getUserPlots(User user);

    // Find Plots liked by a user
    List<Plot> findByLikesContainsUser(User user);
    
    // Method to increment views
    Plot incrementPlotViews(Long plotId) throws PlotException;
    
	public Plot updatePlot(Long plotId,Plot reqPlot)throws UserException;

	public Plot viewPlot(Long plotId) throws PlotException, UserException;

}
