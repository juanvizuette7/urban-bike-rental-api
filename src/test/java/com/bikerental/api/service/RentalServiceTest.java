package com.bikerental.api.service;

import com.bikerental.api.dto.FinishRentalRequestDTO;
import com.bikerental.api.dto.RentalResponseDTO;
import com.bikerental.api.dto.StartRentalRequestDTO;
import com.bikerental.api.exception.BicycleNotAvailableException;
import com.bikerental.api.exception.InvalidRentalException;
import com.bikerental.api.exception.ResourceNotFoundException;
import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import com.bikerental.api.model.Rental;
import com.bikerental.api.model.RentalStatus;
import com.bikerental.api.repository.BicycleRepository;
import com.bikerental.api.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BicycleRepository bicycleRepository;

    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalService = new RentalService(rentalRepository, bicycleRepository, new PricingService());
    }

    @Test
    void startRentalWithAvailableBicycleCreatesActiveRentalAndMarksBicycleAsRented() {
        Bicycle bicycle = bicycle("BIC-001", BicycleType.URBANA, BicycleStatus.DISPONIBLE);
        StartRentalRequestDTO request = new StartRentalRequestDTO("BIC-001", "Juan Perez", 2);

        when(bicycleRepository.findByCode("BIC-001")).thenReturn(Optional.of(bicycle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> {
            Rental rental = invocation.getArgument(0);
            rental.setId(1L);
            return rental;
        });

        RentalResponseDTO response = rentalService.startRental(request);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());
        verify(bicycleRepository).save(bicycle);

        Rental savedRental = rentalCaptor.getValue();
        assertEquals(RentalStatus.ACTIVO, savedRental.getStatus());
        assertEquals(BicycleStatus.ALQUILADA, bicycle.getStatus());
        assertEquals(RentalStatus.ACTIVO, response.status());
        assertEquals("BIC-001", response.bicycleCode());
    }

    @Test
    void startRentalWithBicycleInMaintenanceThrowsBicycleNotAvailableException() {
        Bicycle bicycle = bicycle("BIC-004", BicycleType.MONTANA, BicycleStatus.EN_MANTENIMIENTO);
        StartRentalRequestDTO request = new StartRentalRequestDTO("BIC-004", "Ana Ruiz", 3);

        when(bicycleRepository.findByCode("BIC-004")).thenReturn(Optional.of(bicycle));

        assertThrows(BicycleNotAvailableException.class, () -> rentalService.startRental(request));

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(bicycleRepository, never()).save(any(Bicycle.class));
    }

    @Test
    void finishRentalWithActiveRentalFinalizesRentalReleasesBicycleAndCalculatesCosts() {
        Bicycle bicycle = bicycle("BIC-002", BicycleType.MONTANA, BicycleStatus.ALQUILADA);
        Rental rental = activeRental(10L, bicycle, LocalDateTime.of(2026, 5, 16, 10, 0), 2);
        FinishRentalRequestDTO request = new FinishRentalRequestDTO(LocalDateTime.of(2026, 5, 16, 13, 20));

        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalResponseDTO response = rentalService.finishRental(10L, request);

        verify(bicycleRepository).save(bicycle);
        verify(rentalRepository).save(rental);

        assertEquals(RentalStatus.FINALIZADO, rental.getStatus());
        assertEquals(BicycleStatus.DISPONIBLE, bicycle.getStatus());
        assertEquals(4, rental.getRealUsedHours());
        assertEquals(2, rental.getLateHours());
        assertEquals(BigDecimal.valueOf(20000), rental.getBaseCost());
        assertEquals(BigDecimal.valueOf(5000), rental.getPenaltyCost());
        assertEquals(BigDecimal.valueOf(25000), rental.getTotalCost());

        assertEquals(RentalStatus.FINALIZADO, response.status());
        assertEquals(4, response.realUsedHours());
        assertEquals(BigDecimal.valueOf(20000), response.baseCost());
        assertEquals(BigDecimal.valueOf(5000), response.penaltyCost());
        assertEquals(BigDecimal.valueOf(25000), response.totalCost());
        assertTrue(response.hasPenalty());
    }

    @Test
    void finishRentalWithAlreadyFinishedRentalThrowsInvalidRentalException() {
        Bicycle bicycle = bicycle("BIC-003", BicycleType.ELECTRICA, BicycleStatus.DISPONIBLE);
        Rental rental = activeRental(20L, bicycle, LocalDateTime.of(2026, 5, 16, 10, 0), 2);
        rental.setStatus(RentalStatus.FINALIZADO);

        when(rentalRepository.findById(20L)).thenReturn(Optional.of(rental));

        InvalidRentalException exception = assertThrows(
                InvalidRentalException.class,
                () -> rentalService.finishRental(20L, new FinishRentalRequestDTO(LocalDateTime.of(2026, 5, 16, 12, 0)))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(rentalRepository, never()).save(any(Rental.class));
        verify(bicycleRepository, never()).save(any(Bicycle.class));
    }

    @Test
    void finishRentalWithMissingRentalThrowsResourceNotFoundException() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> rentalService.finishRental(99L, new FinishRentalRequestDTO(LocalDateTime.of(2026, 5, 16, 12, 0)))
        );

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(bicycleRepository, never()).save(any(Bicycle.class));
    }

    private Bicycle bicycle(String code, BicycleType type, BicycleStatus status) {
        Bicycle bicycle = new Bicycle(code, type, status);
        bicycle.setId(1L);
        return bicycle;
    }

    private Rental activeRental(Long id, Bicycle bicycle, LocalDateTime startTime, int estimatedDurationHours) {
        Rental rental = new Rental(bicycle, "Cliente Test", startTime, estimatedDurationHours, RentalStatus.ACTIVO);
        rental.setId(id);
        return rental;
    }
}
