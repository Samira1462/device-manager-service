package com.codechallenge.devicemanagerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDto {
    private Long id;
    private String name;
    private String brand;
    private String state;
    private Instant createdAt;
    private Instant updatedAt;
}
