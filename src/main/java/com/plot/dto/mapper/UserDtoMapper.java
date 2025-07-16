package com.plot.dto.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Import for Stream.concat

import com.plot.dto.UserDto;
import com.plot.dto.UserSummaryDto;
import com.plot.dto.WalletBasicDto;
import com.plot.dto.NotificationDto;
import com.plot.dto.WalletTransactionBasicDto; // Ensure this is a basic DTO
import com.plot.models.User;
import com.plot.models.Wallet;
import com.plot.models.Notification; // Ensure this is imported for mapping

public class UserDtoMapper {

    // You might need a WalletTransactionMapper and NotificationMapper
    // to map the entities to their DTOs safely.
    // For now, let's assume they are simple and map directly or you'll create these.
    // public static WalletTransactionDto toWalletTransactionDto(WalletTransaction tx) { ... }
    // public static NotificationDto toNotificationDto(Notification noti) { ... }


    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setImage(user.getImage());
        userDto.setBackgroundImage(user.getBackgroundImage());
        userDto.setBio(user.getBio());
        userDto.setEducation(user.getEducation());
        userDto.setPhone(user.getPhone());
        userDto.setWork(user.getWork());
        userDto.setBirthDate(user.getBirthDate());
        userDto.setFollowers(toUserDtos(user.getFollowers()));
        userDto.setFollowing(toUserDtos(user.getFollowings()));
        userDto.setLogin_with_google(user.isLogin_with_google());
        userDto.setLocation(user.getLocation());
        userDto.setCreated_at(user.getCreated_at());
        

        // 1) Wallet Mapping
        if (user.getWallet() != null) {
            Wallet w = user.getWallet();
            WalletBasicDto walletBasicDto = new WalletBasicDto();
            walletBasicDto.setId(w.getId());
            walletBasicDto.setLikesBalance(w.getLikesBalance());
            walletBasicDto.setTotalLikesReceived(w.getTotalLikesReceived());
            walletBasicDto.setSystemWallet(w.isSystemWallet());
            
            //add userInfo
            UserSummaryDto userSummaryDto = new UserSummaryDto();
            userSummaryDto.setId(user.getId());
            userSummaryDto.setFullName(user.getFullName());
            userSummaryDto.setEmail(user.getEmail());
            userSummaryDto.setImage(user.getImage());
            walletBasicDto.setUserSummaryDto(userSummaryDto);// Assuming WalletBasicDto has userId
            
            
            userDto.setWallet(walletBasicDto);
            
        } else {
            // Optional: If a user might not have a wallet, set it to null or default
            userDto.setWallet(null);
        }

        // 2) Wallet Transactions Mapping - Retrieve via the user's wallet
        if (user.getWallet() != null) {
            Wallet userWallet = user.getWallet();
            List<WalletTransactionBasicDto> allUserTransactions = new ArrayList<>();

            // Combine sent and received transactions
            if (userWallet.getSentTransactions() != null) {
                allUserTransactions.addAll(userWallet.getSentTransactions().stream()
                    .map(WalletTransactionDtoMapper::toWalletTransactionDto) // Needs WalletTransactionMapper
                    .collect(Collectors.toList()));
            }
            if (userWallet.getReceivedTransactions() != null) {
                 allUserTransactions.addAll(userWallet.getReceivedTransactions().stream()
                    .map(WalletTransactionDtoMapper::toWalletTransactionDto) // Needs WalletTransactionMapper
                    .collect(Collectors.toList()));
            }
            // Sort by transaction time if desired
            allUserTransactions.sort((tx1, tx2) -> tx2.getTransactionTime().compareTo(tx1.getTransactionTime()));

            userDto.setWalletTransactions(allUserTransactions);
        } else {
            userDto.setWalletTransactions(new ArrayList<>()); // Ensure it's an empty list if no wallet
        }


        // 3) Notifications Mapping (assuming Notification is directly on User, or via other means)
        // If 'notifications' is truly not on User, you'd fetch them via a NotificationService
        // based on the user's ID. If it IS on User but was simply overlooked:
        if (user.getNotifications() != null) {
             userDto.setNotifications(user.getNotifications().stream()
                                        .map(NotificationDtoMapper::toNotificationDto) // Needs NotificationMapper
                                        .collect(Collectors.toList()));
        } else {
             userDto.setNotifications(new ArrayList<>());
        }


        return userDto;
    }

    public static List<UserDto> toUserDtos(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
            .map(user -> {
                UserDto dto = new UserDto();
                dto.setId(user.getId());
                dto.setEmail(user.getEmail());
                dto.setFullName(user.getFullName());
                dto.setImage(user.getImage());
                dto.setCreated_at(user.getCreated_at());
                dto.setFollowerCount((long)user.getFollowers().size());
                dto.setFollowingCount((long)user.getFollowings().size());
                // Crucial: Do NOT map nested wallet, followers, following, notifications here
                // to keep it partial and prevent infinite recursion.
                return dto;
            })
            .collect(Collectors.toList());
    }
}