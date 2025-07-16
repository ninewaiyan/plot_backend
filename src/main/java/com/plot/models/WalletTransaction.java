package com.plot.models;

import java.time.LocalDateTime;

import com.plot.enums.TransactionType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class WalletTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Wallet senderWallet;  // Null if system is sender (for BUY)

	@ManyToOne
	private Wallet receiverWallet; // Null if system is receiver (for EXCHANGE)

	private Long  amount; // Number of likes transferred or purchased

	@Enumerated(EnumType.STRING)
	private TransactionType type;

	private String description;

	private LocalDateTime transactionTime = LocalDateTime.now();
}
