package com.plot.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plot.models.Plot;
import com.plot.models.User;

public interface PlotRepository extends JpaRepository<Plot, Long> {

    // Fetch all main plots (not replies), ordered by newest first
    List<Plot> findAllByIsPlotTrueOrderByCreatedAtDesc();

    // Fetch plots where the user is the author or has re-plotted it
    List<Plot> findByCollectedUsersContainsOrUser_IdAndIsPlotTrueOrderByCreatedAtDesc(User user, Long userId);

    // Fetch plots that a user liked (if you're using a direct likes collection)
    List<Plot> findByLikesContainingOrderByCreatedAtDesc(User user);

    // Custom JPQL: Fetch plots liked by userId
    @Query("SELECT p FROM Plot p JOIN p.likes l WHERE l.user.id = :userId")
    List<Plot> findByLikesUserId(Long userId);
    
//    @Query("SELECT p FROM Plot p " +
//            "ORDER BY " +
//            "    CASE " +
//            "        WHEN p.user.id IN (SELECT f.targetUser.id FROM Follow f WHERE f.sourceUser.id = :currentUserId) THEN 1 " + // Plots by users CURRENT_USER is FOLLOWING
//            "        WHEN p.user.id IN (SELECT f.sourceUser.id FROM Follow f WHERE f.targetUser.id = :currentUserId) THEN 2 " + // Plots by users FOLLOWING CURRENT_USER
//            "        ELSE 3 " + // All other plots
//            "    END ASC, " + // Order by priority (1 then 2 then 3)
//            "    p.createdAt DESC") // Then by creation date descending within each priority group
//     List<Plot> findFeedPlotsOrderedByFollowingFollowersAndDate(@Param("currentUserId") Long currentUserId);
}
