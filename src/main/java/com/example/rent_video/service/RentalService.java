package com.example.rent_video.service;

import com.example.rent_video.dto.RentalRequest;
import com.example.rent_video.dto.RentalResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface RentalService {

    RentalResponse rentVideo(RentalRequest request) throws BadRequestException;

    RentalResponse returnVideo(Long rentalId) throws BadRequestException;

    List<RentalResponse> getMyRentals();

    RentalResponse getRentalById(Long id);
}