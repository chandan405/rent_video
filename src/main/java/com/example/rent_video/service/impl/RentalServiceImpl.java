package com.example.rent_video.service.impl;

import com.example.rent_video.dto.RentalRequest;
import com.example.rent_video.dto.RentalResponse;
import com.example.rent_video.entity.*;
import com.example.rent_video.helper.CurrentUserService;
import com.example.rent_video.repository.RentalRepository;
import com.example.rent_video.repository.VideoRepository;
import com.example.rent_video.service.RentalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final VideoRepository videoRepository;
    private final CurrentUserService currentUserService;

    @Override
    public RentalResponse rentVideo(RentalRequest request) throws BadRequestException {

        User user = currentUserService.getCurrentUser();

        Video video = videoRepository.findById(request.getVideoId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Video not found: "
                                        + request.getVideoId()
                        ));

        if (video.getStatus() == VideoStatus.DELETED) {
            throw new RuntimeException(
                    "Video not found: " + request.getVideoId()
            );
        }

        if (video.getAvailableCopies() <= 0) {
            throw new BadRequestException(
                    "Video is currently unavailable"
            );
        }

        boolean alreadyRented =
                rentalRepository
                        .existsByUserIdAndVideoIdAndStatus(
                                user.getId(),
                                video.getId(),
                                RentalStatus.RENTED
                        );

        if (alreadyRented) {
            throw new BadRequestException(
                    "You have already rented this video"
            );
        }

        video.setAvailableCopies(
                video.getAvailableCopies() - 1
        );

        if (video.getAvailableCopies() == 0) {
            video.setStatus(VideoStatus.UNAVAILABLE);
        }

        video.setUpdatedAt(LocalDateTime.now());

        videoRepository.save(video);

        Rental rental = Rental.builder()
                .user(user)
                .video(video)
                .status(RentalStatus.RENTED)
                .rentedAt(LocalDateTime.now())
                .build();

        Rental savedRental = rentalRepository.save(rental);

        return mapToResponse(savedRental);
    }

    @Override
    public RentalResponse returnVideo(Long rentalId) throws BadRequestException {

        User user = currentUserService.getCurrentUser();

        Rental rental = rentalRepository
                .findByIdAndUserId(rentalId, user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found: " + rentalId
                        ));

        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new BadRequestException(
                    "Video has already been returned"
            );
        }

        Video video = rental.getVideo();

        video.setAvailableCopies(
                video.getAvailableCopies() + 1
        );

        video.setStatus(VideoStatus.AVAILABLE);
        video.setUpdatedAt(LocalDateTime.now());

        videoRepository.save(video);

        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnedAt(LocalDateTime.now());

        Rental updatedRental =
                rentalRepository.save(rental);

        return mapToResponse(updatedRental);
    }

    @Override
    @Transactional()
    public List<RentalResponse> getMyRentals() {

        User user = currentUserService.getCurrentUser();

        return rentalRepository
                .findByUserIdOrderByRentedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional()
    public RentalResponse getRentalById(Long id) {

        User user = currentUserService.getCurrentUser();

        Rental rental = rentalRepository
                .findByIdAndUserId(id, user.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Rental not found: " + id
                        ));

        return mapToResponse(rental);
    }

    private RentalResponse mapToResponse(Rental rental) {

        return RentalResponse.builder()
                .id(rental.getId())
                .videoId(rental.getVideo().getId())
                .videoTitle(rental.getVideo().getTitle())
                .status(rental.getStatus())
                .rentedAt(rental.getRentedAt())
                .returnedAt(rental.getReturnedAt())
                .build();
    }

}