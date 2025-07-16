package com.plot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferRequestDto {
	
	    @NotNull(message = "Amount cannot be null")
	    @Min(value = 1, message = "Amount must be at least 1")
	    private Long amount; // Using 'amount' for consistency with WalletTransactionDto

	    @NotNull(message = "Receiver ID cannot be null")
	    @Min(value = 1, message = "Receiver ID must be positive")
	    private Long receiverId;

	    private String description; // Optional: good for adding context

}