package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.dto.DeviceUpdateDto;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DeviceService {

    DeviceResponseDto save(DeviceCreateDto dto);

    DeviceResponseDto updateDevice(Long id, DeviceUpdateDto dto);

    Page<DeviceResponseDto> getAllDevice(Pageable pageable);

    Optional<DeviceResponseDto> getById(Long id);

    void deleteDevice(Long id);

    Page<DeviceResponseDto> findDevices(String brand, DeviceState state, Pageable pageable);
}
