package com.bikerental.api.controller;

import com.bikerental.api.dto.RentalResponseDTO;
import com.bikerental.api.dto.StartRentalRequestDTO;
import com.bikerental.api.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDTO startRental(@Valid @RequestBody StartRentalRequestDTO request) {
        return rentalService.startRental(request);
    }
}
