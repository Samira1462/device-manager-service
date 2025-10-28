package com.codechallenge.devicemanagerservice.controllers;

import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.dto.DeviceUpdateDto;
import com.codechallenge.devicemanagerservice.dto.PagedResponseDto;
import com.codechallenge.devicemanagerservice.exception.DeviceNotFoundException;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import com.codechallenge.devicemanagerservice.service.DeviceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/devices")
public class DeviceManagerControllers {

    private final Logger logger = LoggerFactory.getLogger(DeviceManagerControllers.class);

    private final DeviceService deviceService;

    public DeviceManagerControllers(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceResponseDto> createDevice(@RequestBody @Valid DeviceCreateDto dto) {
        logger.info("Received an inbound request to save a device");
        var created = deviceService.save(dto);

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> updateDevice(
            @PathVariable Long id,
            @RequestBody @Valid DeviceUpdateDto dto
    ) {
        DeviceResponseDto updated = deviceService.updateDevice(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDto> getDevice(@PathVariable Long id) {
        DeviceResponseDto device = deviceService.getById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        return ResponseEntity.ok(device);
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<DeviceResponseDto>> getAllDevices(Pageable pageable) {

        logger.info("Received request to fetch all devices (page={}, size={})", pageable.getPageNumber(), pageable.getPageSize());

        Page<DeviceResponseDto> devicesPage = deviceService.getAllDevice(pageable);

        PagedResponseDto<DeviceResponseDto> response = new PagedResponseDto<>(
                devicesPage.getContent(),
                devicesPage.getNumber(),
                devicesPage.getSize(),
                devicesPage.getTotalElements(),
                devicesPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponseDto<DeviceResponseDto>> searchDevices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) DeviceState state,
            Pageable pageable) {

        logger.info("Received request to search devices with brand='{}', state='{}' , (page={}, size={})", brand, state,
                pageable.getPageNumber(), pageable.getPageSize());


        Page<DeviceResponseDto> devicesPage = deviceService.findDevices(brand, state, pageable);
        PagedResponseDto<DeviceResponseDto> response = new PagedResponseDto<>(
                devicesPage.getContent(),
                devicesPage.getNumber(),
                devicesPage.getSize(),
                devicesPage.getTotalElements(),
                devicesPage.getTotalPages()
        );
        return ResponseEntity.ok(response);
    }
}
