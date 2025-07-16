package com.plot.sevices.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.plot.exceptions.UserException;
import com.plot.exceptions.WalletException;
import com.plot.models.User;
import com.plot.models.Wallet;
import com.plot.repositories.WalletRepository;
import com.plot.services.WalletService;
import java.util.Optional;



@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getWalletByUser(User user) throws UserException, WalletException {
        Wallet wallet = walletRepository.findByUser(user);
        if (wallet == null) {
            throw new WalletException("Wallet not found for user: " + user.getId());
        }
        return wallet;
    }
    
    @Override
    public Wallet getSystemWallet() throws WalletException {
        return walletRepository.findByIsSystemWalletTrue()
                .orElseThrow(() -> new WalletException("System wallet not found"));
    }
    

	@Override
	public void addLikes(User user, int amount) throws UserException, WalletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Wallet createWalletForUser(User user) throws UserException, WalletException {
		// TODO Auto-generated method stub
		Wallet wallet = new Wallet();
		wallet.setUser(user);
		wallet.setLikesBalance(10);
		wallet.setTotalLikesReceived(0);
		wallet.setSystemWallet(false);
		walletRepository.save(wallet);
		
		return null;
	}

}
