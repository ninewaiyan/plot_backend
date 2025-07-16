package com.plot.controllers;

import javax.sound.midi.Receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plot.dto.NotificationDto;
import com.plot.dto.TransferRequestDto;
import com.plot.dto.WalletTransactionBasicDto;
import com.plot.dto.WalletTransactionDto;
import com.plot.enums.NotificationType;
import com.plot.enums.TransactionType;
import com.plot.exceptions.NotificationException;
import com.plot.exceptions.PlotException;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.exceptions.WalletTransactionException;
import com.plot.models.User;
import com.plot.models.Wallet;
import com.plot.models.WalletTransaction;
import com.plot.repositories.NotificationRepository;
import com.plot.repositories.UserRepository;
import com.plot.repositories.WalletRepository;
import com.plot.repositories.WalletTransactionRepository;
import com.plot.services.NotificationService;
import com.plot.services.UserService;
import com.plot.services.WalletService;
import com.plot.services.WalletTransactionService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

	@Autowired
	private WalletService walletService;


	@Autowired
	private WalletTransactionService  walletTransactionService;


	@Autowired
	private UserService userService;


	@PostMapping("/buy")
	public ResponseEntity<?> buyingHandle(@RequestParam Long reqAmount,
			@RequestHeader("Authorization") String jwt)
					throws UserException, PlotException {

		try {
			User user = userService.findUserProfileByJwt(jwt);
			Wallet userWallet = walletService.getWalletByUser(user);

			// Create transaction
			WalletTransactionDto newTransaction = new WalletTransactionDto();
			Wallet systemWallet = walletService.getSystemWallet();
			newTransaction.setSenderWallet(systemWallet);
			newTransaction.setReceiverWallet(userWallet);
			newTransaction.setAmount(reqAmount);
			newTransaction.setType(TransactionType.BUY);
			WalletTransaction savedTransaction = walletTransactionService.createBuyTransaction(newTransaction);
			return ResponseEntity.ok("Ok");

		} catch (UserException | WalletException | WalletTransactionException  e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}


	@PostMapping("/exchange")
	public ResponseEntity<?> exchangeHandle(@RequestParam Long reqAmount,
			@RequestHeader("Authorization") String jwt)
					throws UserException, PlotException {
		try {
			User user = userService.findUserProfileByJwt(jwt);
			Wallet userWallet = walletService.getWalletByUser(user);

			// Create transaction
			WalletTransactionDto newTransaction = new WalletTransactionDto();
			Wallet systemWallet = walletService.getSystemWallet();
			newTransaction.setSenderWallet(userWallet);
			newTransaction.setReceiverWallet(systemWallet);
			newTransaction.setAmount(reqAmount);
			newTransaction.setType(TransactionType.EXCHANGE);
			WalletTransaction savedTransaction = walletTransactionService.createExchangeTransaction(newTransaction);
			return ResponseEntity.ok("Ok");

		} catch (UserException | WalletException | WalletTransactionException  e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/transfer")
	public ResponseEntity<?> transferHandle(@RequestBody TransferRequestDto tranReqDto,
			@RequestHeader("Authorization") String jwt)
					throws UserException, PlotException {
		try {
			
			User senderUser = userService.findUserProfileByJwt(jwt);
			Wallet senderWallet = walletService.getWalletByUser(senderUser);
			
			if(tranReqDto.getReceiverId() == null) {
                throw new WalletTransactionException("Receiver Id is null");
			}
			
			User receiverUser = userService.findUserById(tranReqDto.getReceiverId());
			Wallet receiverWallet = walletService.getWalletByUser(receiverUser);

			// Create transaction
			WalletTransactionDto newTransaction = new WalletTransactionDto();
			newTransaction.setSenderWallet(senderWallet);
			newTransaction.setReceiverWallet(receiverWallet);
			newTransaction.setAmount(tranReqDto.getAmount());
			newTransaction.setType(TransactionType.TRANSFER);
			newTransaction.setDescription(tranReqDto.getDescription());
			WalletTransaction savedTransaction = walletTransactionService.createTranferTransaction(newTransaction);
			return ResponseEntity.ok("Ok");

		} catch (UserException | WalletException | WalletTransactionException  e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

