package com.plot.services;

import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.User;
import com.plot.models.Wallet;

public interface WalletService {
	
	    Wallet getWalletByUser(User user) throws UserException,WalletException;

	    void addLikes(User user, int amount) throws UserException,WalletException;

	    Wallet getSystemWallet() throws WalletException;
	    
	    Wallet createWalletForUser (User user) throws UserException,WalletException;
	 
	    
}
