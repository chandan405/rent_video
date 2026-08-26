package com.example.rent_video.repository;

import com.example.rent_video.entity.Rental;
import com.example.rent_video.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserIdOrderByRentedAtDesc(Long userId);

    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndVideoIdAndStatus(
            Long userId,
            Long videoId,
            RentalStatus status
    );
}
