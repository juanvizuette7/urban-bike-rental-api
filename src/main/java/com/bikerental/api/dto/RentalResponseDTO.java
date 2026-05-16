package com.bikerental.api.dto;

import com.bikerental.api.model.BicycleType;
import com.bikerental.api.model.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RentalResponseDTO(
        Long id,
        String bicycleCode,
        BicycleType bicycleType,
        String customerName,
        LocalDateTime startTime,
        Integer estimatedDurationHours,
        LocalDateTime returnTime,
        Integer realUsedHours,
        Integer lateHours,
        BigDecimal baseCost,
        BigDecimal penaltyCost,
        BigDecimal totalCost,
        RentalStatus status,
        boolean hasPenalty
) {
}
