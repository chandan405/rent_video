package com.example.rent_video.service.impl;

import com.example.rent_video.dto.VideoRequest;
import com.example.rent_video.dto.VideoResponse;
import com.example.rent_video.entity.Video;
import com.example.rent_video.entity.VideoStatus;
import com.example.rent_video.repository.VideoRepository;
import com.example.rent_video.service.VideoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;

    @Override
    public VideoResponse createVideo(VideoRequest request) {
        if(videoRepository.existsByTitleIgnoreCase(request.getTitle())){
            throw new RuntimeException("Video already exists with title: " + request.getTitle());
        }

        Video video = Video.builder().
                title(request.getTitle()).
                director(request.getDirector()).genre(request.getGenre())
                .releaseYear(request.getReleaseYear()).
                availableCopies(request.getTotalCopies())
                .totalCopies(request.getTotalCopies())
                .status(VideoStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();

        Video savedVideo = videoRepository.save(video);
        return  mapToResponse(savedVideo);

    }

    @Override
    public Page<VideoResponse> getVideos(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        Page<Video> videos =
                videoRepository.findByStatusNot(
                        VideoStatus.DELETED,
                        pageable
                );

        return videos.map(this::mapToResponse);
    }

    @Override
    public VideoResponse getVideoById(Long id) {

        Video video  = videoRepository.findById(id).orElseThrow(()-> new RuntimeException("video is not found with id "+id));

        if(video.getStatus() == VideoStatus.DELETED){
            throw new RuntimeException("video is not found with id "+id);
        }

        return mapToResponse(video);
    }

    @Override
    public VideoResponse updateVideo(Long id, VideoRequest request) throws BadRequestException {
        Video video =  videoRepository.findById(id).orElseThrow(()-> new RuntimeException("video is not found with id "+id));
        if(video.getStatus() == VideoStatus.DELETED){
            throw new RuntimeException("video is not found with id "+id);
        }

        if(!video.getTitle().equalsIgnoreCase(request.getTitle()) && videoRepository.existsByTitleIgnoreCase(request.getTitle())){
            throw new RuntimeException("video is already present with this title");
        }

        video.setTitle(request.getTitle());
        video.setDirector(request.getDirector());
        video.setGenre(request.getGenre());
        video.setReleaseYear(request.getReleaseYear());

        int rentedcopies = video.getTotalCopies() - video.getAvailableCopies();

        if(request.getTotalCopies()<rentedcopies){
            throw new BadRequestException(
                    "Total copies cannot be less than currently rented copies"
            );
        }
        video.setTotalCopies(request.getTotalCopies());
        video.setAvailableCopies(
                request.getTotalCopies() - rentedcopies
        );

        video.setUpdatedAt(LocalDateTime.now());

        Video updatedVideo = videoRepository.save(video);

        return mapToResponse(updatedVideo);
    }

    @Override
    public void deleteVideo(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Video not found with id: " + id
                        ));

        if (video.getStatus() == VideoStatus.DELETED) {
            throw new RuntimeException(
                    "Video not found with id: " + id
            );
        }

        video.setStatus(VideoStatus.DELETED);
        video.setUpdatedAt(LocalDateTime.now());

        videoRepository.save(video);
    }

    private VideoResponse mapToResponse(Video video) {

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .director(video.getDirector())
                .genre(video.getGenre())
                .releaseYear(video.getReleaseYear())
                .availableCopies(video.getAvailableCopies())
                .totalCopies(video.getTotalCopies())
                .status(video.getStatus().name())
                .build();
    }
}
