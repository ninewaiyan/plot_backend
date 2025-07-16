package com.plot.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    // single string holding media URL(s) or JSON list of media URLs
    private String media;
    
    private Long views = 0L;
    

    @OneToMany(mappedBy = "plot", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "replotFor", cascade = CascadeType.ALL)
    private List<Plot> rePlots = new ArrayList<>();

    @ManyToMany
    private List<User> collectedUsers = new ArrayList<>();

    @ManyToOne
    private Plot replotFor;

    private boolean isRePlot;

    private boolean isPlot;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

}
