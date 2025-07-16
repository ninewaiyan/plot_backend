package com.plot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
public class WalletDto {
    private Long id;
    

    @JsonIgnoreProperties({
        "followers", "following", "wallet", "walletTransactions",
        "notifications", "reqUser", "login_with_google", "followed"
    })
    private UserDto user;  // can be null for system wallet
    private Long likesBalance;
    private Long totalLikesReceived;
    private boolean isSystemWallet;
}
