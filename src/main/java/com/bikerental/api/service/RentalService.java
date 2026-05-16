package com.bikerental.api.service;

import com.bikerental.api.dto.FinishRentalRequestDTO;
import com.bikerental.api.dto.RentalHistoryResponseDTO;
import com.bikerental.api.dto.RentalResponseDTO;
import com.bikerental.api.dto.StartRentalRequestDTO;
import com.bikerental.api.exception.BicycleNotAvailableException;
import com.bikerental.api.exception.InvalidRentalException;
import com.bikerental.api.exception.ResourceNotFoundException;
import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.Rental;
import com.bikerental.api.model.RentalStatus;
import com.bikerental.api.repository.BicycleRepository;
import com.bikerental.api.repository.RentalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BicycleRepository bicycleRepository;
    private final PricingService pricingService;

    public RentalService(RentalRepository rentalRepository, BicycleRepository bicycleRepository,
                         PricingService pricingService) {
        this.rentalRepository = rentalRepository;
        this.bicycleRepository = bicycleRepository;
        this.pricingService = pricingService;
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

    @Transactional
    public RentalResponseDTO finishRental(Long rentalId, FinishRentalRequestDTO request) {
        Rental rental = findRentalByIdOrThrow(rentalId);

        if (rental.getStatus() == RentalStatus.FINALIZADO) {
            throw new InvalidRentalException("El alquiler ya fue finalizado", HttpStatus.CONFLICT);
        }

        LocalDateTime returnTime = request == null || request.returnTime() == null
                ? LocalDateTime.now()
                : request.returnTime();

        if (!returnTime.isAfter(rental.getStartTime())) {
            throw new InvalidRentalException("La fecha de retorno debe ser posterior a la fecha de inicio");
        }

        Bicycle bicycle = rental.getBicycle();
        int realUsedHours = pricingService.calculateRoundedHours(rental.getStartTime(), returnTime);
        int lateHours = pricingService.calculateLateHours(
                rental.getStartTime(),
                returnTime,
                rental.getEstimatedDurationHours()
        );
        BigDecimal baseCost = pricingService.calculateBaseCost(bicycle.getType(), realUsedHours);
        BigDecimal penaltyCost = pricingService.calculatePenaltyCost(bicycle.getType(), lateHours);
        BigDecimal totalCost = pricingService.calculateTotalCost(baseCost, penaltyCost);

        rental.setReturnTime(returnTime);
        rental.setRealUsedHours(realUsedHours);
        rental.setLateHours(lateHours);
        rental.setBaseCost(baseCost);
        rental.setPenaltyCost(penaltyCost);
        rental.setTotalCost(totalCost);
        rental.setStatus(RentalStatus.FINALIZADO);
        bicycle.setStatus(BicycleStatus.DISPONIBLE);

        bicycleRepository.save(bicycle);
        Rental savedRental = rentalRepository.save(rental);

        return toResponse(savedRental);
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> findAll() {
        return rentalRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RentalResponseDTO findById(Long rentalId) {
        return toResponse(findRentalByIdOrThrow(rentalId));
    }

    @Transactional(readOnly = true)
    public List<RentalHistoryResponseDTO> findHistoryByBicycleCode(String code) {
        if (!bicycleRepository.existsByCode(code)) {
            throw new ResourceNotFoundException("No existe una bicicleta con el codigo: " + code);
        }

        return rentalRepository.findByBicycleCodeOrderByStartTimeDesc(code)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    private Rental findRentalByIdOrThrow(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un alquiler con el id: " + rentalId));
    }

    private RentalHistoryResponseDTO toHistoryResponse(Rental rental) {
        boolean hasPenalty = rental.getPenaltyCost() != null
                && rental.getPenaltyCost().compareTo(BigDecimal.ZERO) > 0;

        return new RentalHistoryResponseDTO(
                rental.getId(),
                rental.getCustomerName(),
                rental.getStartTime(),
                rental.getReturnTime(),
                rental.getRealUsedHours(),
                rental.getTotalCost(),
                hasPenalty
        );
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
