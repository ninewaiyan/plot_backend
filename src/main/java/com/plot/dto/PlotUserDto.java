package com.plot.dto;

import lombok.Data;

@Data
public class PlotUserDto {

	private Long id;
	private String fullName;
	private String email;
	private String image;
    private WalletBasicDto wallet;

}
