package com.example.rent_video.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {

    private Long id;
    private String title;
    private String director;
    private String genre;
    private Integer releaseYear;
    private Integer availableCopies;
    private Integer totalCopies;
    private String status;
}