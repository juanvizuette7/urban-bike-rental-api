package com.bikerental.api.service;

import com.bikerental.api.exception.InvalidRentalException;
import com.bikerental.api.model.BicycleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void calculateRoundedHoursReturnsTwoWhenUsageIsOneHourAndTenMinutes() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 15, 11, 10);

        int roundedHours = pricingService.calculateRoundedHours(start, end);

        assertEquals(2, roundedHours);
    }

    @Test
    void calculateRoundedHoursReturnsTwoWhenUsageIsExactlyTwoHours() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 15, 12, 0);

        int roundedHours = pricingService.calculateRoundedHours(start, end);

        assertEquals(2, roundedHours);
    }

    @Test
    void getHourlyRateReturnsRateByBicycleType() {
        assertEquals(BigDecimal.valueOf(3500), pricingService.getHourlyRate(BicycleType.URBANA));
        assertEquals(BigDecimal.valueOf(5000), pricingService.getHourlyRate(BicycleType.MONTANA));
        assertEquals(BigDecimal.valueOf(7500), pricingService.getHourlyRate(BicycleType.ELECTRICA));
    }

    @Test
    void completeMountainBikeCostCase() {
        BicycleType type = BicycleType.MONTANA;
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 10, 0);
        LocalDateTime returnedAt = LocalDateTime.of(2026, 5, 15, 13, 20);
        int estimatedDurationHours = 2;

        int realUsedHours = pricingService.calculateRoundedHours(start, returnedAt);
        BigDecimal baseCost = pricingService.calculateBaseCost(type, realUsedHours);
        int lateHours = pricingService.calculateLateHours(start, returnedAt, estimatedDurationHours);
        BigDecimal penaltyCost = pricingService.calculatePenaltyCost(type, lateHours);
        BigDecimal totalCost = pricingService.calculateTotalCost(baseCost, penaltyCost);

        assertEquals(4, realUsedHours);
        assertEquals(BigDecimal.valueOf(20000), baseCost);
        assertEquals(2, lateHours);
        assertEquals(BigDecimal.valueOf(5000), penaltyCost);
        assertEquals(BigDecimal.valueOf(25000), totalCost);
    }

    @Test
    void calculateRoundedHoursThrowsInvalidRentalExceptionWhenEndIsEqualToStart() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 10, 0);

        assertThrows(InvalidRentalException.class, () -> pricingService.calculateRoundedHours(start, start));
    }

    @Test
    void calculateRoundedHoursThrowsInvalidRentalExceptionWhenEndIsBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 15, 9, 59);

        assertThrows(InvalidRentalException.class, () -> pricingService.calculateRoundedHours(start, end));
    }
}
