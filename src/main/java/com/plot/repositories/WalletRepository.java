package com.plot.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plot.models.User;
import com.plot.models.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
	
	Optional<Wallet> findByIsSystemWalletTrue();

    Wallet findByUserId(Long userId);
    
    Wallet findByUser(User user);

}
