package com.example.rent_video.controller;

import com.example.rent_video.dto.RentalRequest;
import com.example.rent_video.dto.RentalResponse;
import com.example.rent_video.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RentalResponse> rentVideo(
            @Valid @RequestBody RentalRequest request) throws BadRequestException {

        RentalResponse response =
                rentalService.rentVideo(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RentalResponse> returnVideo(
            @PathVariable Long id) throws BadRequestException {

        return ResponseEntity.ok(
                rentalService.returnVideo(id)
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RentalResponse>> getMyRentals() {

        return ResponseEntity.ok(
                rentalService.getMyRentals()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RentalResponse> getRental(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                rentalService.getRentalById(id)
        );
    }
}
