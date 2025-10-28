package com.codechallenge.devicemanagerservice.swagger;

import com.codechallenge.devicemanagerservice.dto.*;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface DeviceManagerApi {
    @Operation(
            summary = "Create a new device",
            description = "Creates a new device and returns the created resource with its URI location."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<DeviceResponseDto> createDevice(@Valid @RequestBody DeviceCreateDto dto);

    @Operation(
            summary = "Update existing device",
            description = "Updates device fields, restricted if device is in use."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<DeviceResponseDto> updateDevice(@PathVariable Long id,
                                                   @Valid @RequestBody DeviceUpdateDto dto);

    @Operation(summary = "get device", description = "get a device by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "get deleted"),
            @ApiResponse(responseCode = "404", description = "get not found"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<DeviceResponseDto> getDevice(@PathVariable @Min(1) Long id);

    @Operation(summary = "get all device", description = "get all device.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "get all device")
    })
    ResponseEntity<PagedResponseDto<DeviceResponseDto>> getAllDevices(Pageable pageable);

    @Operation(summary = "Delete device", description = "Deletes a device unless it’s in use.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deleted"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    ResponseEntity<Void> deleteDevice(@PathVariable @Min(1) Long id);

    @Operation(summary = "search device", description = "search all device base on brand or state.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "get all device base on brand or state.")
    })
    ResponseEntity<PagedResponseDto<DeviceResponseDto>> searchDevices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) DeviceState state,
            Pageable pageable);
}


