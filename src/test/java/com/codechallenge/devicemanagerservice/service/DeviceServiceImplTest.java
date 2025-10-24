package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.AbstractTest;
import com.codechallenge.devicemanagerservice.dto.DeviceDto;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import com.codechallenge.devicemanagerservice.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceServiceImplTest extends AbstractTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceServiceImpl systemUnderTest;

    @BeforeEach
    void setUpEach() {
        deviceRepository.deleteAll();
    }

    @Nested
    class SaveTests {

        @Test
        void save_givenValidDto_thenReturnDeviceDto() {
            // given
            DeviceDto dto = new DeviceDto();
            dto.setName("MacBook Pro M3");
            dto.setBrand("Apple");
            dto.setState("AVAILABLE");

            // when
            Optional<DeviceDto> result = systemUnderTest.save(dto);

            // then
            assertThat(result).isPresent();
            DeviceDto saved = result.get();

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("MacBook Pro M3");
            assertThat(saved.getBrand()).isEqualTo("Apple");
            assertThat(saved.getState()).isEqualTo("AVAILABLE");

            DeviceEntity entity = deviceRepository.findById(saved.getId()).orElseThrow();
            assertThat(entity.getName()).isEqualTo("MacBook Pro M3");
            assertThat(entity.getBrand()).isEqualTo("Apple");
            assertThat(entity.getState()).isEqualTo(DeviceState.AVAILABLE);
        }
    }
}