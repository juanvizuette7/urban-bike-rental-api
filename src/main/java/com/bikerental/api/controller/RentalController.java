package com.bikerental.api.controller;

import com.bikerental.api.dto.FinishRentalRequestDTO;
import com.bikerental.api.dto.RentalResponseDTO;
import com.bikerental.api.dto.StartRentalRequestDTO;
import com.bikerental.api.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PutMapping("/{id}/finish")
    public RentalResponseDTO finishRental(@PathVariable Long id,
                                          @Valid @RequestBody(required = false) FinishRentalRequestDTO request) {
        return rentalService.finishRental(id, request);
    }

    @GetMapping
    public List<RentalResponseDTO> findAll() {
        return rentalService.findAll();
    }

    @GetMapping("/{id}")
    public RentalResponseDTO findById(@PathVariable Long id) {
        return rentalService.findById(id);
    }
}
