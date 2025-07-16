package com.plot.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
public class Wallet {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private User user; // Null for system wallet

	// Exclude these from toString() to prevent LazyInitializationException
	@ToString.Exclude
	@OneToMany(mappedBy = "senderWallet", cascade = CascadeType.ALL)
	private List<WalletTransaction> sentTransactions = new ArrayList<>();

	// Exclude these from toString() to prevent LazyInitializationException
	@ToString.Exclude
	@OneToMany(mappedBy = "receiverWallet", cascade = CascadeType.ALL)
	private List<WalletTransaction> receivedTransactions = new ArrayList<>();

	private long likesBalance;
	private long totalLikesReceived;

	private boolean isSystemWallet = false; // This flag marks the system wallet

	// You don't need to manually write toString(), getters, setters, etc.,
	// as Lombok's @Data will handle them.
}
