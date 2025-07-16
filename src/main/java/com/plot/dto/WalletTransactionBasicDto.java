package com.plot.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.plot.enums.TransactionType;
import com.plot.models.Wallet;

import lombok.Data;

@Data
public class WalletTransactionBasicDto {
	private Long id;
	private WalletBasicDto senderWallet;    // null if system is sender
	private WalletBasicDto receiverWallet;  // null if system is receiver
	private Long amount;
	private TransactionType type;            // e.g., "TRANSFER", "BUY"
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime transactionTime;
}
