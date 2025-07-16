package com.plot.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // User who liked
    @ManyToOne
    private User user;

    // Plot which was liked
    @ManyToOne
    private Plot plot;

    // Optional: timestamp when like was created (helpful for analytics)
    private LocalDateTime likedAt = LocalDateTime.now();
}
