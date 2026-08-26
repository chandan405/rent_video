package com.example.rent_video.repository;

import com.example.rent_video.entity.Video;
import com.example.rent_video.entity.VideoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    boolean existsByTitleIgnoreCase(String title);

    Optional<Video> findByIdAndStatusNot(
            Long id,
            VideoStatus status
    );

    Page<Video> findByStatusNot(
            VideoStatus status,
            Pageable pageable
    );
}