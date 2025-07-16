package com.plot.requests;


import lombok.Data;

@Data
public class ReplotRequest {

	private Long plotId;
	private String content;
	private String media;

}
