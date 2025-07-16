package com.plot.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plot.models.Like;
import com.plot.models.Plot;
import com.plot.models.User;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a user has already liked a plot
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.plot.id = :plotId")
    Like isLikeExist(@Param("userId") Long userId, @Param("plotId") Long plotId);

    // Get all likes for a specific plot
    @Query("SELECT l FROM Like l WHERE l.plot.id = :plotId")
    List<Like> findByPlotId(@Param("plotId") Long plotId);
    
    Optional<Like> findByUserAndPlot(User user, Plot plot);
}
