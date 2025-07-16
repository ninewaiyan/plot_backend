package com.plot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String fullName;
    private String email;

    private String image;
    private String backgroundImage;
    private String location;
    private String phone;
    private String work;
    private String education;
    private String bio;
    
    private Long followerCount;
    private Long followingCount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;

    private boolean reqUser;
    private boolean login_with_google;

    private WalletBasicDto wallet;

    private List<WalletTransactionBasicDto> walletTransactions = new ArrayList<>();

    @JsonIgnoreProperties({  "walletTransactions", "notifications"})
    private List<UserDto> followers = new ArrayList<>();

    @JsonIgnoreProperties({"followers", "following", "walletTransactions", "notifications"})
    private List<UserDto> following = new ArrayList<>();

    private List<NotificationDto> notifications = new ArrayList<>();

    private boolean followed;
}
