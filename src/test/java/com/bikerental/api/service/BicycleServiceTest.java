package com.bikerental.api.service;

import com.bikerental.api.dto.BicycleRequestDTO;
import com.bikerental.api.dto.BicycleResponseDTO;
import com.bikerental.api.exception.DuplicateResourceException;
import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import com.bikerental.api.repository.BicycleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BicycleServiceTest {

    @Mock
    private BicycleRepository bicycleRepository;

    private BicycleService bicycleService;

    @BeforeEach
    void setUp() {
        bicycleService = new BicycleService(bicycleRepository);
    }

    @Test
    void findAvailableWithoutTypeFilterReturnsAllAvailableBicycles() {
        when(bicycleRepository.findByStatus(BicycleStatus.DISPONIBLE)).thenReturn(List.of(
                bicycle(1L, "BIC-001", BicycleType.URBANA, BicycleStatus.DISPONIBLE),
                bicycle(2L, "BIC-002", BicycleType.MONTANA, BicycleStatus.DISPONIBLE)
        ));

        List<BicycleResponseDTO> response = bicycleService.findAvailable(null);

        assertEquals(2, response.size());
        assertEquals("BIC-001", response.get(0).code());
        assertEquals("BIC-002", response.get(1).code());
        verify(bicycleRepository).findByStatus(BicycleStatus.DISPONIBLE);
        verify(bicycleRepository, never()).findByStatusAndType(BicycleStatus.DISPONIBLE, BicycleType.URBANA);
    }

    @Test
    void findAvailableWithTypeFilterReturnsAvailableBicyclesByType() {
        when(bicycleRepository.findByStatusAndType(BicycleStatus.DISPONIBLE, BicycleType.URBANA)).thenReturn(List.of(
                bicycle(1L, "BIC-001", BicycleType.URBANA, BicycleStatus.DISPONIBLE)
        ));

        List<BicycleResponseDTO> response = bicycleService.findAvailable(BicycleType.URBANA);

        assertEquals(1, response.size());
        assertEquals(BicycleType.URBANA, response.get(0).type());
        verify(bicycleRepository).findByStatusAndType(BicycleStatus.DISPONIBLE, BicycleType.URBANA);
        verify(bicycleRepository, never()).findByStatus(BicycleStatus.DISPONIBLE);
    }

    @Test
    void createRejectsDuplicatedCode() {
        BicycleRequestDTO request = new BicycleRequestDTO(
                "BIC-001",
                BicycleType.URBANA,
                BicycleStatus.DISPONIBLE
        );

        when(bicycleRepository.existsByCode("BIC-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> bicycleService.create(request));

        verify(bicycleRepository, never()).save(org.mockito.ArgumentMatchers.any(Bicycle.class));
    }

    private Bicycle bicycle(Long id, String code, BicycleType type, BicycleStatus status) {
        Bicycle bicycle = new Bicycle(code, type, status);
        bicycle.setId(id);
        return bicycle;
    }
}
