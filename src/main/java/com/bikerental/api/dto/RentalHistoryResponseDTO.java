package com.bikerental.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RentalHistoryResponseDTO(
        Long rentalId,
        String customerName,
        LocalDateTime startTime,
        LocalDateTime returnTime,
        Integer realUsedHours,
        BigDecimal totalCost,
        boolean hasPenalty
) {
}
