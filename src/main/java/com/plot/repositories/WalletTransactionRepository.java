package com.plot.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletTransactionException;
import com.plot.models.User;
import com.plot.models.WalletTransaction;

	public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

	    List<WalletTransaction> findBySenderWallet_Id(Long senderWalletId);

	    List<WalletTransaction> findByReceiverWallet_Id(Long receiverWalletId);

	    List<WalletTransaction> findBySenderWallet_IdOrReceiverWallet_Id(Long senderWalletId, Long receiverWalletId);
	}


