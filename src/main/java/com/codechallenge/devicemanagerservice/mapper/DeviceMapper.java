package com.codechallenge.devicemanagerservice.mapper;

import com.codechallenge.devicemanagerservice.dto.DeviceDto;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;

public final class DeviceMapper {

    private DeviceMapper() {
    }

    public static DeviceEntity createDeviceEntity(DeviceDto deviceDto) {
        var entity = new DeviceEntity();
        entity.setName(deviceDto.getName());
        entity.setBrand(deviceDto.getBrand());
        entity.setState(DeviceState.valueOf(deviceDto.getState()));
        return entity;
    }

    public static DeviceDto createDeviceDto(DeviceEntity deviceEntity) {
        var deviceDto = new DeviceDto();
        deviceDto.setName(deviceEntity.getName());
        deviceDto.setBrand(deviceEntity.getBrand());
        deviceDto.setState(String.valueOf(deviceEntity.getState()));
        deviceDto.setId(deviceEntity.getId());
        return deviceDto;
    }

}
