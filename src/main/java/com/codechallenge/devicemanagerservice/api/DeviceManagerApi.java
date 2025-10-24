package com.codechallenge.devicemanagerservice.api;

import com.codechallenge.devicemanagerservice.dto.DeviceDto;
import com.codechallenge.devicemanagerservice.exception.DeviceSaveException;
import com.codechallenge.devicemanagerservice.service.DeviceService;
import com.codechallenge.devicemanagerservice.utils.ApiErrorUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static com.codechallenge.devicemanagerservice.utils.ApiErrorUtils.shouldBeValid;

@RestController
@RequestMapping("/app")
public class DeviceManagerApi {

    private final Logger logger = LoggerFactory.getLogger(DeviceManagerApi.class);

    private final DeviceService deviceService;

    public DeviceManagerApi(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/devices")
    @Validated
    public ResponseEntity<DeviceDto> create(@RequestBody @Valid DeviceDto dto, BindingResult bindingResult) {
        logger.info("Received an inbound request to save a device");
        shouldBeValid(bindingResult);

        return deviceService.save(dto)
                .map(savedDevice -> ResponseEntity
                        .created(URI.create("/app/devices/" + savedDevice.getId()))
                        .body(savedDevice))
                .orElseThrow(() -> new DeviceSaveException("Unable to save device"));
    }


    @GetMapping("/history")
    public ResponseEntity<String> getPostalCode() {
        return new ResponseEntity<>("Samira", HttpStatus.OK);
    }
}
