package com.bikerental.api.dto;

import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;

public record BicycleResponseDTO(
        Long id,
        String code,
        BicycleType type,
        BicycleStatus status
) {
}
