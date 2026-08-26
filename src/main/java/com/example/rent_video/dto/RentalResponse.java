package com.example.rent_video.dto;

import com.example.rent_video.entity.RentalStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RentalResponse {

    private Long id;

    private Long videoId;

    private String videoTitle;

    private RentalStatus status;

    private LocalDateTime rentedAt;

    private LocalDateTime returnedAt;
}