package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.dto.DeviceUpdateDto;
import com.codechallenge.devicemanagerservice.exception.DeviceNotFoundException;
import com.codechallenge.devicemanagerservice.exception.DeviceOperationException;
import com.codechallenge.devicemanagerservice.exception.DeviceUpdateException;
import com.codechallenge.devicemanagerservice.mapper.DeviceMapper;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import com.codechallenge.devicemanagerservice.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    private final DeviceRepository repository;

    public DeviceServiceImpl(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeviceResponseDto save(DeviceCreateDto dto) {
        var entity = repository.save(DeviceMapper.createDeviceEntity(dto));
        logger.info("Device created with ID: {}", entity.getId());
        return DeviceMapper.createDeviceDto(entity);
    }

    @Transactional
    @Override
    public DeviceResponseDto updateDevice(Long id, DeviceUpdateDto dto) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (existing.getState() == DeviceState.IN_USE) {
            if ((dto.getName() != null && !dto.getName().equals(existing.getName())) ||
                    (dto.getBrand() != null && !dto.getBrand().equals(existing.getBrand()))) {
                throw new DeviceUpdateException(
                        "Cannot update name or brand when device is in use."
                );
            }
        }

        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getBrand() != null) existing.setBrand(dto.getBrand());
        if (dto.getState() != null) {
            try {
                existing.setState(dto.getState());
            } catch (IllegalArgumentException e) {
                throw new DeviceUpdateException("Invalid state value: " + dto.getState());
            }
        }

        existing.setUpdatedAt(Instant.now());
        DeviceEntity saved = repository.save(existing);

        return DeviceMapper.createDeviceDto(saved);
    }

    @Override
    public Optional<DeviceResponseDto> getById(Long id) {
        return repository.findById(id)
                .map(DeviceMapper::createDeviceDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DeviceResponseDto> getAllDevice(Pageable pageable) {
        return repository.findAll(pageable)
                .map(DeviceMapper::createDeviceDto);
    }

    @Transactional
    @Override
    public void deleteDevice(Long id) {
        logger.info("Attempting to delete device with ID: {}", id);
        DeviceEntity device = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Device with ID {} not found", id);
                    return new DeviceNotFoundException(id);
                });

        if (device.getState() == DeviceState.IN_USE) {
            logger.warn("Cannot delete device with ID {}: device is in use", id);
            throw new DeviceOperationException("Cannot delete device in use");
        }

        repository.delete(device);
        logger.info("Device with ID {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<DeviceResponseDto> findDevices(String brand, DeviceState state, Pageable pageable) {
        logger.info("Searching devices with brand='{}' and state='{}'", brand, state);

        String stateName = state != null ? state.name() : null;
        Specification<DeviceEntity> spec = DeviceSpecification.searchBy(brand, stateName);

        Page<DeviceEntity> page = repository.findAll(spec, pageable);
        return page.map(DeviceMapper::createDeviceDto);
    }


}
