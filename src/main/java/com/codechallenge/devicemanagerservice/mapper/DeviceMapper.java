package com.codechallenge.devicemanagerservice.mapper;

import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;


public final class DeviceMapper {

    private DeviceMapper() {
    }

    public static DeviceEntity createDeviceEntity(DeviceCreateDto deviceDto) {
        var entity = new DeviceEntity();
        entity.setName(deviceDto.getName());
        entity.setBrand(deviceDto.getBrand());
        entity.setState(deviceDto.getState());
        return entity;
    }

    public static DeviceResponseDto createDeviceDto(DeviceEntity deviceEntity) {
        var deviceResponseDto = new DeviceResponseDto();
        deviceResponseDto.setName(deviceEntity.getName());
        deviceResponseDto.setBrand(deviceEntity.getBrand());
        deviceResponseDto.setState(String.valueOf(deviceEntity.getState()));
        deviceResponseDto.setId(deviceEntity.getId());
        deviceResponseDto.setCreatedAt(deviceEntity.getCreatedAt());
        deviceResponseDto.setUpdatedAt(deviceEntity.getUpdatedAt());
        return deviceResponseDto;
    }

}
