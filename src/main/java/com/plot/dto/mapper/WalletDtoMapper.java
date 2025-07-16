package com.plot.dto.mapper;

import org.springframework.stereotype.Component;

import com.plot.dto.WalletDto;
import com.plot.models.Wallet;

@Component
public class WalletDtoMapper {
    public WalletDto toDto(Wallet wallet) {
        if (wallet == null) return null;
        WalletDto dto = new WalletDto();
        dto.setId(wallet.getId());
        dto.setLikesBalance(wallet.getLikesBalance());
        dto.setTotalLikesReceived(wallet.getTotalLikesReceived());
        dto.setSystemWallet(wallet.isSystemWallet());
        // To avoid recursion, omit setting full UserDto here
        return dto;
    }
}