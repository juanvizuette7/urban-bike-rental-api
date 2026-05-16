package com.bikerental.api.dto;

import java.time.LocalDateTime;

public record FinishRentalRequestDTO(
        LocalDateTime returnTime
) {
}
