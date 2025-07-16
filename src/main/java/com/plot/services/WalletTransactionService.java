package com.plot.services;

import java.util.List;

import com.plot.dto.WalletTransactionBasicDto;
import com.plot.dto.WalletTransactionDto;
import com.plot.enums.TransactionType;
import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletTransactionException;
import com.plot.models.User;
import com.plot.models.WalletTransaction;

public interface WalletTransactionService {
	
	    WalletTransaction createBuyTransaction(WalletTransactionDto walletTranDto) throws UserException,WalletTransactionException;
	    WalletTransaction createExchangeTransaction(WalletTransactionDto walletTranDto) throws UserException,WalletTransactionException;
	    WalletTransaction createTranferTransaction(WalletTransactionDto walletTranDto) throws UserException,WalletTransactionException;



	    List<WalletTransaction> getTransactionsForUser(User user) throws UserException,WalletTransactionException;

}
