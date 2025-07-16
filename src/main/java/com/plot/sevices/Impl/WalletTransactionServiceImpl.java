package com.plot.sevices.Impl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.plot.dto.NotificationDto;
import com.plot.dto.UserSummaryDto;
import com.plot.dto.WalletTransactionBasicDto;
import com.plot.dto.WalletTransactionDto;
import com.plot.enums.NotificationType;
import com.plot.enums.TransactionType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletTransactionException;
import com.plot.models.User;
import com.plot.models.Wallet;
import com.plot.models.WalletTransaction;
import com.plot.repositories.WalletRepository;
import com.plot.repositories.WalletTransactionRepository;
import com.plot.services.NotificationService;
import com.plot.services.UserService;
import com.plot.services.WalletTransactionService;
import com.plot.util.UserUtil;

import jakarta.transaction.Transactional;



@Service
public class WalletTransactionServiceImpl implements WalletTransactionService {


	@Autowired
	private WalletTransactionRepository walletTransactionRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private UserService userService; 

	@Override
	public List<WalletTransaction> getTransactionsForUser(User user) throws UserException, WalletTransactionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional // Ensures atomicity: all operations succeed or all rollback
	public WalletTransaction createBuyTransaction(WalletTransactionDto walletTranDto)
			throws UserException, WalletTransactionException {

		Wallet senderWalletEntity = null;
		if (walletTranDto.getSenderWallet() != null && walletTranDto.getSenderWallet().getId() != null) {
			senderWalletEntity = walletRepository.findById(walletTranDto.getSenderWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Sender wallet not found with ID: " + walletTranDto.getSenderWallet().getId()));
		}

		Wallet receiverWalletEntity = null;
		if (walletTranDto.getReceiverWallet() != null && walletTranDto.getReceiverWallet().getId() != null) {
			receiverWalletEntity = walletRepository.findById(walletTranDto.getReceiverWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Receiver wallet not found with ID: " + walletTranDto.getReceiverWallet().getId()));
		}


		WalletTransaction newTransaction = new WalletTransaction();
		newTransaction.setAmount(walletTranDto.getAmount());
		newTransaction.setType(walletTranDto.getType());
		newTransaction.setDescription(walletTranDto.getDescription());
		newTransaction.setTransactionTime(LocalDateTime.now()); // Set current time for the transaction
		newTransaction.setSenderWallet(senderWalletEntity);
		newTransaction.setReceiverWallet(receiverWalletEntity);


		if (receiverWalletEntity == null) {
			throw new WalletTransactionException("Receiver wallet is required for BUY type transaction.");
		}
		if (newTransaction.getAmount() == null || newTransaction.getAmount() <= 0) {
			throw new WalletTransactionException("Buy amount must be positive.");
		}

		if (senderWalletEntity == null) {
			throw new WalletTransactionException("System sender wallet not found for BUY type transaction. Cannot process purchase.");
		}

		if (senderWalletEntity.getLikesBalance() < newTransaction.getAmount()) {
			throw new WalletTransactionException("Insufficient balance in sender wallet.");
		}

		if (newTransaction.getAmount() > senderWalletEntity.getLikesBalance()) {
			throw new WalletTransactionException(
					"Currently Available: " + senderWalletEntity.getLikesBalance()
					);
		}

		receiverWalletEntity.setLikesBalance(receiverWalletEntity.getLikesBalance() + newTransaction.getAmount());
		senderWalletEntity.setLikesBalance(senderWalletEntity.getLikesBalance() - newTransaction.getAmount());

		walletRepository.save(receiverWalletEntity);
		walletRepository.save(senderWalletEntity);


		// This is done AFTER balance updates to ensure atomicity within the @Transactional block.
		WalletTransaction savedTransaction = walletTransactionRepository.save(newTransaction);


		// 6. Prepare and send Notification (safely handling nulls)
		NotificationDto newNotiDto = new NotificationDto();



		User senderUser = savedTransaction.getSenderWallet().getUser();


		if(senderUser != null) {
			newNotiDto.setSenderUser(UserUtil.changeUserSummary(senderUser));
		}else {
			newNotiDto.setSenderUser(null);
		}


		User receiverUser = savedTransaction.getReceiverWallet().getUser();

		if(receiverUser != null) {
			newNotiDto.setReceiverUser(UserUtil.changeUserSummary(receiverUser));
		}else {
			newNotiDto.setReceiverUser(null);
		}


		// Determine NotificationType and generate a descriptive message
		NotificationType notificationType;
		String notificationMessage;
		notificationType = NotificationType.BUY;
		notificationMessage = "successfully purchased " + savedTransaction.getAmount() + " likes.";
		newNotiDto.setMessage(notificationMessage);
		newNotiDto.setType(notificationType);

		try {
			// Call the notification service method with the DTO and the message
			notificationService.createNewNotification(newNotiDto);
		} catch (NotificationException e) {
			System.err.println("Failed to create notification for transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error but don't prevent transaction commit
		} catch (UserException e) {
			System.err.println("Failed to find user for notification (sender/receiver) during transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error
		}

		// 7. Return the successfully created and saved transaction
		return savedTransaction;
	}

	@Override
	@Transactional
	public WalletTransaction createExchangeTransaction(WalletTransactionDto walletTranDto)
			throws UserException, WalletTransactionException {

		Wallet senderWalletEntity = null;
		if (walletTranDto.getSenderWallet() != null && walletTranDto.getSenderWallet().getId() != null) {


			senderWalletEntity = walletRepository.findById(walletTranDto.getSenderWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Sender wallet not found with ID: " + walletTranDto.getSenderWallet().getId()));
		}

		Wallet receiverWalletEntity = null;
		if (walletTranDto.getReceiverWallet() != null && walletTranDto.getReceiverWallet().getId() != null) {
			receiverWalletEntity = walletRepository.findById(walletTranDto.getReceiverWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Receiver wallet not found with ID: " + walletTranDto.getReceiverWallet().getId()));
		}


		WalletTransaction newTransaction = new WalletTransaction();
		newTransaction.setAmount(walletTranDto.getAmount());
		newTransaction.setType(walletTranDto.getType());
		newTransaction.setDescription(walletTranDto.getDescription());
		newTransaction.setTransactionTime(LocalDateTime.now()); // Set current time for the transaction


		newTransaction.setSenderWallet(senderWalletEntity);
		newTransaction.setReceiverWallet(receiverWalletEntity);

		if (senderWalletEntity == null) {
			throw new WalletTransactionException("Receiver wallet is required for Exchange type transaction.");
		}

		if (receiverWalletEntity == null) {
			throw new WalletTransactionException("Receiver wallet is required for Exchange type transaction.");
		}
		if (newTransaction.getAmount() == null || newTransaction.getAmount() <= 0) {
			throw new WalletTransactionException("Invalid Exchange Amount.");
		}

		if (senderWalletEntity.getLikesBalance() < newTransaction.getAmount()) {
			throw new WalletTransactionException("Insufficient balance For Excange in sender wallet.");
		}


		receiverWalletEntity.setLikesBalance(receiverWalletEntity.getLikesBalance() + newTransaction.getAmount());
		senderWalletEntity.setLikesBalance(senderWalletEntity.getLikesBalance() - newTransaction.getAmount());

		walletRepository.save(receiverWalletEntity);
		walletRepository.save(senderWalletEntity);

		WalletTransaction savedTransaction = walletTransactionRepository.save(newTransaction);

		NotificationDto newNotiDto = new NotificationDto();

		// add User info summary

		User senderUser = savedTransaction.getSenderWallet().getUser();

		if(senderUser != null) {
			newNotiDto.setSenderUser(UserUtil.changeUserSummary(senderUser));
		}else {
			newNotiDto.setSenderUser(null);
		}

		User receiverUser = savedTransaction.getReceiverWallet().getUser();

		if(receiverUser != null) {
			newNotiDto.setReceiverUser(UserUtil.changeUserSummary(receiverUser));
		}else {
			newNotiDto.setReceiverUser(null);
		}




		newNotiDto.setType(NotificationType.EXCHANGE); 
		String notificationMessage = "You have successfully exchange" + savedTransaction.getAmount() + " likes.";
		newNotiDto.setMessage(notificationMessage);

		try {
			// Call the notification service method with the DTO and the message
			notificationService.createNewNotification(newNotiDto);
		} catch (NotificationException e) {
			System.err.println("Failed to create notification for transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error but don't prevent transaction commit
		} catch (UserException e) {
			System.err.println("Failed to find user for notification (sender/receiver) during transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error
		}

		// 7. Return the successfully created and saved transaction
		return savedTransaction;
	}


	@Override
	@Transactional
	public WalletTransaction createTranferTransaction(WalletTransactionDto walletTranDto)
			throws UserException, WalletTransactionException {

		Wallet senderWalletEntity = null;
		if (walletTranDto.getSenderWallet() != null && walletTranDto.getSenderWallet().getId() != null) {


			senderWalletEntity = walletRepository.findById(walletTranDto.getSenderWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Sender wallet not found with ID: " + walletTranDto.getSenderWallet().getId()));
		}

		Wallet receiverWalletEntity = null;
		if (walletTranDto.getReceiverWallet() != null && walletTranDto.getReceiverWallet().getId() != null) {
			receiverWalletEntity = walletRepository.findById(walletTranDto.getReceiverWallet().getId())
					.orElseThrow(() -> new WalletTransactionException("Receiver wallet not found with ID: " + walletTranDto.getReceiverWallet().getId()));
		}


		WalletTransaction newTransaction = new WalletTransaction();
		newTransaction.setAmount(walletTranDto.getAmount());
		newTransaction.setType(walletTranDto.getType());
		newTransaction.setDescription(walletTranDto.getDescription());
		newTransaction.setTransactionTime(LocalDateTime.now()); // Set current time for the transaction


		newTransaction.setSenderWallet(senderWalletEntity);
		newTransaction.setReceiverWallet(receiverWalletEntity);

		if (senderWalletEntity == null) {
			throw new WalletTransactionException("Receiver wallet is required for Transfer type transaction.");
		}

		if (receiverWalletEntity == null) {
			throw new WalletTransactionException("Receiver wallet is required for transfer type transaction.");
		}
		if (newTransaction.getAmount() == null || newTransaction.getAmount() <= 0) {
			throw new WalletTransactionException("Invalid Amount.");
		}

		if (senderWalletEntity.getLikesBalance() < newTransaction.getAmount()) {
			throw new WalletTransactionException("Insufficient balance to Transfer");
		}


		receiverWalletEntity.setLikesBalance(receiverWalletEntity.getLikesBalance() + newTransaction.getAmount());
		senderWalletEntity.setLikesBalance(senderWalletEntity.getLikesBalance() - newTransaction.getAmount());

		walletRepository.save(receiverWalletEntity);
		walletRepository.save(senderWalletEntity);

		WalletTransaction savedTransaction = walletTransactionRepository.save(newTransaction);

		NotificationDto newNotiDto = new NotificationDto();

		User senderUser = savedTransaction.getSenderWallet().getUser();

		if(senderUser != null) {
			newNotiDto.setSenderUser(UserUtil.changeUserSummary(senderUser));
		}else {
			newNotiDto.setSenderUser(null);
		}

		User receiverUser = savedTransaction.getReceiverWallet().getUser();

		if(receiverUser != null) {
			newNotiDto.setReceiverUser(UserUtil.changeUserSummary(receiverUser));
		}else {
			newNotiDto.setReceiverUser(null);
		}



		newNotiDto.setType(NotificationType.TRANSFER); 
		String notificationMessage =  " transfers you " + savedTransaction.getAmount() + "likes";
		newNotiDto.setMessage(notificationMessage);

		try {
			// Call the notification service method with the DTO and the message
			notificationService.createNewNotification(newNotiDto);
		} catch (NotificationException e) {
			System.err.println("Failed to create notification for transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error but don't prevent transaction commit
		} catch (UserException e) {
			System.err.println("Failed to find user for notification (sender/receiver) during transaction " + savedTransaction.getId() + ": " + e.getMessage());
			e.printStackTrace(); // Log the error
		}

		// 7. Return the successfully created and saved transaction
		return savedTransaction;

	}



}


