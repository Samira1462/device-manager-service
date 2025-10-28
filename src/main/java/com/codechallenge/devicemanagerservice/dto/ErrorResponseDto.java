package com.codechallenge.devicemanagerservice.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDto(
        String code,
        String message,
        Instant timestamp,
        List<FieldError> details
) {
    public record FieldError(
            String field,
            Object rejectedValue,
            String message
    ) {}
}