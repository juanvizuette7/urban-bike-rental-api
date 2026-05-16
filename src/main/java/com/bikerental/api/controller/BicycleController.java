package com.bikerental.api.controller;

import com.bikerental.api.dto.BicycleRequestDTO;
import com.bikerental.api.dto.BicycleResponseDTO;
import com.bikerental.api.dto.RentalHistoryResponseDTO;
import com.bikerental.api.model.BicycleType;
import com.bikerental.api.service.BicycleService;
import com.bikerental.api.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bicycles")
public class BicycleController {

    private final BicycleService bicycleService;
    private final RentalService rentalService;

    public BicycleController(BicycleService bicycleService, RentalService rentalService) {
        this.bicycleService = bicycleService;
        this.rentalService = rentalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BicycleResponseDTO create(@Valid @RequestBody BicycleRequestDTO request) {
        return bicycleService.create(request);
    }

    @GetMapping
    public List<BicycleResponseDTO> findAll() {
        return bicycleService.findAll();
    }

    @GetMapping("/available")
    public List<BicycleResponseDTO> findAvailable(@RequestParam(required = false) BicycleType type) {
        return bicycleService.findAvailable(type);
    }

    @GetMapping("/{code}/rentals")
    public List<RentalHistoryResponseDTO> findRentalHistory(@PathVariable String code) {
        return rentalService.findHistoryByBicycleCode(code);
    }
}
