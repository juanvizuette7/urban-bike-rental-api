package com.bikerental.api.dto;

import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BicycleRequestDTO(
        @NotBlank String code,
        @NotNull BicycleType type,
        @NotNull BicycleStatus status
) {
}
