package com.plot.sevices.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.models.Plot;
import com.plot.models.User;
import com.plot.repositories.PlotRepository;
import com.plot.requests.ReplotRequest;
import com.plot.services.PlotService;

import jakarta.transaction.Transactional;

@Service
public class PlotServiceImpl implements PlotService {

	@Autowired
	private PlotRepository plotRepository;

	@Override
	public Plot createPlot(Plot reqPlot, User user) throws UserException {
		// TODO Auto-generated method stub

		Plot plot = new Plot();
		plot.setContent(reqPlot.getContent());
		plot.setMedia(reqPlot.getMedia());
		plot.setUser(user);
		plot.setRePlot(false);
		plot.setPlot(true);
		plot.setViews(0L);
		plot.setCreatedAt(LocalDateTime.now());

		return plotRepository.save(plot);
	}

	@Override
	public List<Plot> findAllPlots() {
		// TODO Auto-generated method stub
		return plotRepository.findAllByIsPlotTrueOrderByCreatedAtDesc();
	}

	@Override
	public Plot collect(Long plotId, User user) throws UserException, PlotException {
		// TODO Auto-generated method stub
		Plot plot = findById(plotId);
		
		   if (plot.getUser().getId().equals(user.getId())) {
	            throw new UserException("You don't need to collect your own plot.");
	        }

		if(plot.getCollectedUsers().contains(user)) {
			plot.getCollectedUsers().remove(user);	
		}
		else {
			plot.getCollectedUsers().add(user);
		}
		return plotRepository.save(plot);
	}

	@Override
	public Plot findById(Long plotId) throws PlotException {
		// TODO Auto-generated method stub
		Plot plot = plotRepository.findById(plotId)
				.orElseThrow(()->new PlotException("Plot not found with id " + plotId));
		return plot;
	}

	@Override
	public void deletePlotById(Long plotId, Long userId) throws PlotException, UserException {
		// TODO Auto-generated method stub
		Plot plot = findById(plotId);

		if(!userId.equals(plot.getUser().getId())) {
			throw new UserException("You can't delete another user's plot");
		}

		plotRepository.deleteById(plot.getId());

	}

	@Override
	public Plot removeFromCollect(Long plotId, User user) throws PlotException, UserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Plot createReplot(ReplotRequest reqPlot, User user) throws PlotException {
		// TODO Auto-plgenerated method stub
		Plot rePlotFor = findById(reqPlot.getPlotId());

		Plot plot = new Plot();
		plot.setContent(reqPlot.getContent());
		plot.setMedia(reqPlot.getMedia());
		plot.setCreatedAt(LocalDateTime.now());
		plot.setUser(user);
		plot.setRePlot(true);
		plot.setPlot(false);
		plot.setReplotFor(rePlotFor);
		Plot saveRePlot = plotRepository.save(plot);
		rePlotFor.getRePlots().add(saveRePlot);
		plotRepository.save(rePlotFor);
		return rePlotFor;
	}

	@Override
	public List<Plot> getUserPlots(User user) {
		// TODO Auto-generated method stub
		return plotRepository.findByCollectedUsersContainsOrUser_IdAndIsPlotTrueOrderByCreatedAtDesc(user,user.getId());
	}

	@Override
	public List<Plot> findByLikesContainsUser(User user) {
		// TODO Auto-generated method stub
		return plotRepository.findByLikesUserId(user.getId());
	}

//	public List<Plot> getPersonalizedFeed(Long currentUserId) {
//		// This single call will fetch and order plots as per your requirements
//		return plotRepository.findFeedPlotsOrderedByFollowingFollowersAndDate(currentUserId);
//	}

	@Override
	@Transactional // Ensures atomicity for the view count update
	public Plot incrementPlotViews(Long plotId) throws PlotException {
		Optional<Plot> optionalPlot = plotRepository.findById(plotId);
		if (optionalPlot.isEmpty()) {
			throw new PlotException("Plot not found with ID: " + plotId);
		}

		Plot plot = optionalPlot.get();
		plot.setViews(plot.getViews() + 1); // Increment the view count
		return plotRepository.save(plot); // Save the updated plot
	}

	@Override
	public Plot updatePlot(Long plotId, Plot reqPlot) throws UserException {
	    Plot existingPlot = plotRepository.findById(plotId)
	        .orElseThrow(() -> new UserException("Plot not found with id: " + plotId));
	    
	    
	    // Update the fields
	    existingPlot.setContent(reqPlot.getContent());
	    existingPlot.setMedia(reqPlot.getMedia());	    
	    // Optionally update metadata fields
	    existingPlot.setUpdatedAt(LocalDateTime.now()); // if you have updatedAt field

	    // Save the updated plot
	    return plotRepository.save(existingPlot);
	}

	@Override
	public Plot viewPlot(Long plotId) throws PlotException, UserException {
		// TODO Auto-generated method stub
		
		Plot plot = plotRepository.findById(plotId)
		        .orElseThrow(() -> new UserException("Plot not found with id: " + plotId));
		
		plot.setViews(plot.getViews()+1);
		
		return plotRepository.save(plot);
	}

	


}
