package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.dto.DeviceDto;

import java.util.Optional;

public interface DeviceService {

    Optional<DeviceDto> save(DeviceDto dto);
}
