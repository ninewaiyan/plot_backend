package com.plot.dto.mapper;
import com.plot.dto.UserSummaryDto;
import com.plot.dto.WalletBasicDto;
import com.plot.dto.WalletTransactionBasicDto;
import com.plot.models.User;
import com.plot.models.WalletTransaction;

public class WalletTransactionDtoMapper {
	public static WalletTransactionBasicDto toWalletTransactionDto(WalletTransaction tx) {
		WalletTransactionBasicDto dto = new WalletTransactionBasicDto();
		dto.setId(tx.getId());
		dto.setAmount(tx.getAmount());
		dto.setType(tx.getType());
		dto.setDescription(tx.getDescription());
		dto.setTransactionTime(tx.getTransactionTime());

		// Map sender/receiver wallets using WalletBasicDto to prevent recursion
		if (tx.getSenderWallet() != null) {
			WalletBasicDto senderWDto = new WalletBasicDto();
			senderWDto.setId(tx.getSenderWallet().getId());
			senderWDto.setLikesBalance(tx.getSenderWallet().getLikesBalance());


			//‌add User Info
			User  senderUser = tx.getSenderWallet().getUser();

			if(senderUser != null) {
				UserSummaryDto userSummaryDto = new UserSummaryDto();
				userSummaryDto.setId(senderUser.getId());
				userSummaryDto.setFullName(senderUser.getFullName());
				userSummaryDto.setEmail(senderUser.getEmail());
				userSummaryDto.setImage(senderUser.getImage());
				senderWDto.setUserSummaryDto(userSummaryDto);
			}else {
				senderWDto.setUserSummaryDto(null);
			}
			

			dto.setSenderWallet(senderWDto);
		}
		if (tx.getReceiverWallet() != null) {
			WalletBasicDto receiverWDto = new WalletBasicDto();
			receiverWDto.setId(tx.getReceiverWallet().getId());
			receiverWDto.setLikesBalance(tx.getReceiverWallet().getLikesBalance());
			
			//add User Info
			User receiverUser = tx.getReceiverWallet().getUser();
			if(receiverUser != null) {
				UserSummaryDto userSummaryDto = new UserSummaryDto();
				userSummaryDto.setId(receiverUser.getId());
				userSummaryDto.setFullName(receiverUser.getFullName());
				userSummaryDto.setEmail(receiverUser.getEmail());
				userSummaryDto.setImage(receiverUser.getImage());
				receiverWDto.setUserSummaryDto(userSummaryDto);
			}else {
				receiverWDto.setUserSummaryDto(null);
			}
			dto.setReceiverWallet(receiverWDto);
		}
		return dto;
	}
}