package com.plot.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PlotDto {

    private Long id;

    private String content;

    // Single media URL or path
    private String media;

    private PlotUserDto user;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long views;

    private int totalLikes;

    private int totalCollects;  // renamed from totalRetweets

    private int totalReplot;

    private boolean isLiked;

    private boolean isCollected;  // renamed from isRetwit
    
    private boolean isReplot;

    private boolean isPlot;

    private List<Long> collectUserIds = new ArrayList<>();  // renamed from retwitUserId

    private List<PlotDto> rePlots = new ArrayList<>();  // replies to this plot

}
