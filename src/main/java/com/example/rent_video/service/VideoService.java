package com.example.rent_video.service;

import com.example.rent_video.dto.VideoRequest;
import com.example.rent_video.dto.VideoResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VideoService {

    VideoResponse createVideo(VideoRequest request);

    Page<VideoResponse> getVideos(
            int page,
            int size,
            String sortBy,
            String direction
    );

    VideoResponse getVideoById(Long id);

    VideoResponse updateVideo(Long id, VideoRequest request) throws BadRequestException;

    void deleteVideo(Long id);
}