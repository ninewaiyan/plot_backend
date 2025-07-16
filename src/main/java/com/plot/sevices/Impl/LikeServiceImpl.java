package com.plot.sevices.Impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plot.enums.NotificationType;
import com.plot.enums.TransactionType;
import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.Like;
import com.plot.models.Notification;
import com.plot.models.Plot;
import com.plot.models.User;
import com.plot.models.Wallet;
import com.plot.models.WalletTransaction;
import com.plot.repositories.LikeRepository;
import com.plot.repositories.NotificationRepository;
import com.plot.repositories.PlotRepository;
import com.plot.repositories.WalletRepository;
import com.plot.repositories.WalletTransactionRepository;
import com.plot.services.LikeService;
import com.plot.services.PlotService;
import com.plot.services.UserService;

import jakarta.transaction.Transactional;

@Service
public class LikeServiceImpl implements LikeService{
	
	@Autowired
	private LikeRepository likeRepository;
	
	@Autowired 
	private PlotService plotService;
	
	@Autowired
	private PlotRepository plotRepository;
	
	@Autowired
	private WalletRepository walletRepository;
	
	@Autowired
	private WalletTransactionRepository walletTransactionRepository;
	
	 @Autowired
	  private NotificationRepository notificationRepository;
	    

	    @Override
	    @Transactional // Ensure the entire operation (like, wallet update, transaction, notification) is atomic
	    public Like likePlot(Long plotId, User user) throws UserException, PlotException, WalletException {

	        // 1. Fetch Plot
	        Plot plot = plotRepository.findById(plotId)
	                .orElseThrow(() -> new PlotException("Plot not found with ID: " + plotId));

	        // 2. Prevent Liking Own Plot
	        if (plot.getUser().getId().equals(user.getId())) {
	            throw new UserException("You cannot like your own plot.");
	        }

	        // 3. Check for existing Like (Prevent duplicate likes from the same user on the same plot)
	        // This is a common requirement for like features.
	        // You'll need a method in LikeRepository: `Optional<Like> findByUserAndPlot(User user, Plot plot);`
	        if (likeRepository.findByUserAndPlot(user, plot).isPresent()) {
	            throw new UserException("You have already liked this plot.");
	        }


	        // 4. Get Sender and Receiver Wallets
	        Wallet senderWallet = walletRepository.findByUser(user);
	        Wallet receiverWallet = walletRepository.findByUser(plot.getUser());

	        // 5. Validate Sender Balance
	        if (senderWallet.getLikesBalance() <= 0) {
	            throw new WalletException("Insufficient likes balance to perform this action.");
	        }

	        // 6. Perform Like Transfer (Update Wallets)
	        senderWallet.setLikesBalance(senderWallet.getLikesBalance() - 1);
	        receiverWallet.setLikesBalance(receiverWallet.getLikesBalance() + 1);
	        receiverWallet.setTotalLikesReceived(receiverWallet.getTotalLikesReceived() + 1);

	        walletRepository.save(senderWallet);
	        walletRepository.save(receiverWallet);

	        // 7. Save the Like Entity
	        Like like = new Like();
	        like.setUser(user);   // The user who liked
	        like.setPlot(plot);   // The plot that was liked
	        Like savedLike = likeRepository.save(like);

	        // 8. Add Like to Plot's Collection (and save Plot)
	        // This is important if you want plot.getLikes() to reflect the new like immediately
	        // within the same transaction or subsequent operations without re-fetching.
	        plot.getLikes().add(savedLike);
	        plotRepository.save(plot); // Persist the updated plot's likes collection


	        // 9. Save WalletTransaction
	        WalletTransaction txn = new WalletTransaction();
	        txn.setSenderWallet(senderWallet);
	        txn.setReceiverWallet(receiverWallet);
	        txn.setAmount(1L); // Amount of like transferred
	        txn.setType(TransactionType.LIKE);
	        txn.setTransactionTime(LocalDateTime.now());
	        walletTransactionRepository.save(txn);

	        // 10. --- ADD NOTIFICATION LOGIC ---
	        // Create a new Notification object
	        Notification notification = new Notification();
	        notification.setSender(user);             // The user who performed the like
	        notification.setReceiver(plot.getUser()); // The owner of the liked plot
	        notification.setMessage( " liked your plot!"); // Customizable message
	        notification.setType(NotificationType.LIKE); // Set notification type
	        notification.setCreatedAt(LocalDateTime.now());
	        notification.setRead(false); // New notifications are unread by default

	        // Save the notification
	        Notification savedNotification = notificationRepository.save(notification);

	        // Optional: Add notification to the receiver's notifications list
	        // This updates the in-memory object. If plot.getUser() is part of the same
	        // persistence context, its notifications collection will be updated.
	        // If the collection is lazy-loaded and not fetched yet, this might not
	        // immediately populate the list for an entity already in the session.
	        // It's often handled more robustly when fetching User entities for their notifications.
	        plot.getUser().getNotifications().add(savedNotification);
	        // Note: You generally don't need to explicitly save plot.getUser() here
	        // if plot.getUser() is managed by the current transaction and the
	        // Notifications are mappedBy "receiver" (which they are in your User entity).
	        // JPA will often manage the collection if the owning side saves.

	        return savedLike;
	    }
	

	@Override
	public List<Like> getAllLikes(Long plotId) throws PlotException {
		// TODO Auto-generated method stub
		
		Plot plot = plotService.findById(plotId);
		
		List<Like>likes=likeRepository.findByPlotId(plotId);
		
		return likes;
	}

}
