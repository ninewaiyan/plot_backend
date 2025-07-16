package com.plot.dto;

import lombok.Data;

@Data
public class WalletBasicDto {
	
	  private Long id;
//	  	private Long userId;
	  
	  //To update userBasic info in wallet
	  	
	  	private UserSummaryDto userSummaryDto;
	    private Long likesBalance;
	    private Long totalLikesReceived;
	    private boolean isSystemWallet;

}
