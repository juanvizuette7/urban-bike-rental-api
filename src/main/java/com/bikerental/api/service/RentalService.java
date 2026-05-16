package com.bikerental.api.service;

import com.bikerental.api.dto.RentalResponseDTO;
import com.bikerental.api.dto.StartRentalRequestDTO;
import com.bikerental.api.exception.BicycleNotAvailableException;
import com.bikerental.api.exception.ResourceNotFoundException;
import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.Rental;
import com.bikerental.api.model.RentalStatus;
import com.bikerental.api.repository.BicycleRepository;
import com.bikerental.api.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BicycleRepository bicycleRepository;

    public RentalService(RentalRepository rentalRepository, BicycleRepository bicycleRepository) {
        this.rentalRepository = rentalRepository;
        this.bicycleRepository = bicycleRepository;
    }

    @Transactional
    public RentalResponseDTO startRental(StartRentalRequestDTO request) {
        Bicycle bicycle = bicycleRepository.findByCode(request.bicycleCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una bicicleta con el codigo: " + request.bicycleCode()));

        if (bicycle.getStatus() != BicycleStatus.DISPONIBLE) {
            throw new BicycleNotAvailableException(request.bicycleCode());
        }

        Rental rental = new Rental(
                bicycle,
                request.customerName(),
                LocalDateTime.now(),
                request.estimatedDurationHours(),
                RentalStatus.ACTIVO
        );

        bicycle.setStatus(BicycleStatus.ALQUILADA);

        bicycleRepository.save(bicycle);
        Rental savedRental = rentalRepository.save(rental);

        return toResponse(savedRental);
    }

    private RentalResponseDTO toResponse(Rental rental) {
        Bicycle bicycle = rental.getBicycle();
        boolean hasPenalty = rental.getPenaltyCost() != null
                && rental.getPenaltyCost().compareTo(BigDecimal.ZERO) > 0;

        return new RentalResponseDTO(
                rental.getId(),
                bicycle.getCode(),
                bicycle.getType(),
                rental.getCustomerName(),
                rental.getStartTime(),
                rental.getEstimatedDurationHours(),
                rental.getReturnTime(),
                rental.getRealUsedHours(),
                rental.getLateHours(),
                rental.getBaseCost(),
                rental.getPenaltyCost(),
                rental.getTotalCost(),
                rental.getStatus(),
                hasPenalty
        );
    }
}
