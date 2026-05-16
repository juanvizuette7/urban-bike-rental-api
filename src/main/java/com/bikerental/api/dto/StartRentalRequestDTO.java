package com.bikerental.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartRentalRequestDTO(
        @NotBlank String bicycleCode,
        @NotBlank String customerName,
        @NotNull @Positive Integer estimatedDurationHours
) {
}
