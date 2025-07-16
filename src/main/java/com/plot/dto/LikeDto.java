package com.plot.dto;

import lombok.Data;

@Data
public class LikeDto {
    
    private Long id;
    private UserDto user;
    private PlotDto plot;  // Changed from TwitDto to PlotDto

}
