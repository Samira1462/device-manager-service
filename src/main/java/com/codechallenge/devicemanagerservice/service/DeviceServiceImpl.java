package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.dto.DeviceDto;
import com.codechallenge.devicemanagerservice.mapper.DeviceMapper;
import com.codechallenge.devicemanagerservice.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    private final DeviceRepository repository;

    public DeviceServiceImpl(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<DeviceDto> save(DeviceDto dto) {
        var entity = repository.save(DeviceMapper.createDeviceEntity(dto));
        return Optional.of(DeviceMapper.createDeviceDto(entity));
    }


}
