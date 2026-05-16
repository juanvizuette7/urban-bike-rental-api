package com.bikerental.api.service;

import com.bikerental.api.exception.InvalidRentalException;
import com.bikerental.api.model.BicycleType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {

    public BigDecimal getHourlyRate(BicycleType type) {
        return switch (type) {
            case URBANA -> BigDecimal.valueOf(3500);
            case MONTANA -> BigDecimal.valueOf(5000);
            case ELECTRICA -> BigDecimal.valueOf(7500);
        };
    }

    public int calculateRoundedHours(LocalDateTime start, LocalDateTime end) {
        long minutes = Duration.between(start, end).toMinutes();

        if (minutes <= 0) {
            throw new InvalidRentalException("La fecha de retorno debe ser posterior a la fecha de inicio");
        }

        return (int) Math.ceil(minutes / 60.0);
    }

    public int calculateLateHours(LocalDateTime startTime, LocalDateTime returnTime, int estimatedDurationHours) {
        LocalDateTime expectedReturnTime = startTime.plusHours(estimatedDurationHours);

        if (!returnTime.isAfter(expectedReturnTime)) {
            return 0;
        }

        return calculateRoundedHours(expectedReturnTime, returnTime);
    }

    public BigDecimal calculateBaseCost(BicycleType type, int roundedRealUsedHours) {
        return getHourlyRate(type).multiply(BigDecimal.valueOf(roundedRealUsedHours));
    }

    public BigDecimal calculatePenaltyCost(BicycleType type, int lateHours) {
        if (lateHours <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal penaltyRate = getHourlyRate(type).divide(BigDecimal.valueOf(2));
        return penaltyRate.multiply(BigDecimal.valueOf(lateHours));
    }

    public BigDecimal calculateTotalCost(BigDecimal baseCost, BigDecimal penaltyCost) {
        return baseCost.add(penaltyCost);
    }
}
