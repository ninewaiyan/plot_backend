package com.plot.models;

import java.time.LocalDateTime;

import com.plot.enums.NotificationType;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // The user who receives the notification
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // The user who triggered the action (could be null for system-generated events)
    @ManyToOne
    private User sender;

    // Related plot (for likes, replies, replot)
    @ManyToOne
    private Plot plot;

    // Related wallet transaction (for buy, transfer, exchange notifications)
    @OneToOne
    private WalletTransaction transaction;

    // Notification type: LIKE, REPLY, REPLOT, FOLLOW, TRANSFER, EXCHANGE, BUY, SYSTEM
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    // Optional custom message
    private String message;

    private boolean isRead = false;
    
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
