package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.dto.DeviceCreateDto;
import com.codechallenge.devicemanagerservice.dto.DeviceResponseDto;
import com.codechallenge.devicemanagerservice.dto.DeviceUpdateDto;
import com.codechallenge.devicemanagerservice.exception.DeviceNotFoundException;
import com.codechallenge.devicemanagerservice.exception.DeviceOperationException;
import com.codechallenge.devicemanagerservice.exception.DeviceUpdateException;
import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import com.codechallenge.devicemanagerservice.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceServiceImplTest {

    @Mock
    private DeviceRepository repository;

    @InjectMocks
    private DeviceServiceImpl systemUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class SaveTests {

        @Test
        void givenValidDeviceCreateDto_whenSave_shouldReturnSavedDeviceResponseDto() {
      
            DeviceCreateDto dto = new DeviceCreateDto("MacBook Pro", "Apple", DeviceState.valueOf("AVAILABLE"));

            DeviceEntity mockEntity = new DeviceEntity();
            mockEntity.setId(1L);
            mockEntity.setName("MacBook Pro");
            mockEntity.setBrand("Apple");
            mockEntity.setState(DeviceState.AVAILABLE);
            mockEntity.setCreatedAt(Instant.now());
            mockEntity.setUpdatedAt(Instant.now());

     
            when(repository.save(any(DeviceEntity.class))).thenReturn(mockEntity);
            
            DeviceResponseDto response = systemUnderTest.save(dto);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("MacBook Pro");
            assertThat(response.getBrand()).isEqualTo("Apple");
            assertThat(response.getState()).isEqualTo("AVAILABLE");

            verify(repository, times(1)).save(any(DeviceEntity.class));
        }
    }

    @Nested
    class UpdateTests {
        @Test
        void givenNonExistingId_whenUpdate_thenThrowDeviceNotFound() {
            Long id = 1L;
            DeviceUpdateDto dto = new DeviceUpdateDto();

            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThrows(DeviceNotFoundException.class, () -> systemUnderTest.updateDevice(id, dto));
        }

        @Test
        void givenInUseDevice_whenChangeNameOrBrand_thenThrowUpdateException() {
            Long id = 1L;
            DeviceEntity existing = new DeviceEntity();
            existing.setId(id);
            existing.setName("Old");
            existing.setBrand("Brand");
            existing.setState(DeviceState.IN_USE);

            DeviceUpdateDto dto = new DeviceUpdateDto();
            dto.setName("New");

            when(repository.findById(id)).thenReturn(Optional.of(existing));

            DeviceUpdateException ex = assertThrows(DeviceUpdateException.class,
                    () -> systemUnderTest.updateDevice(id, dto));
            assertEquals("Cannot update name or brand when device is in use.", ex.getMessage());
        }

        @Test
        void givenInUseDevice_whenChangeStateValid_thenUpdateSuccessfully() {
            Long id = 1L;
            DeviceEntity existing = new DeviceEntity();
            existing.setId(id);
            existing.setState(DeviceState.IN_USE);

            DeviceUpdateDto dto = new DeviceUpdateDto();
            dto.setState(DeviceState.valueOf("AVAILABLE"));

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            DeviceResponseDto result = systemUnderTest.updateDevice(id, dto);

            assertEquals(DeviceState.AVAILABLE.name(), existing.getState().name());
            assertNotNull(existing.getUpdatedAt());
            assertNotNull(result);
        }

        @Test
        void givenAvailableDevice_whenChangeNameAndBrand_thenUpdateSuccessfully() {
            Long id = 1L;
            DeviceEntity existing = new DeviceEntity();
            existing.setId(id);
            existing.setName("Old");
            existing.setBrand("OldBrand");
            existing.setState(DeviceState.AVAILABLE);

            DeviceUpdateDto dto = new DeviceUpdateDto();
            dto.setName("New");
            dto.setBrand("NewBrand");

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            DeviceResponseDto result = systemUnderTest.updateDevice(id, dto);

            assertEquals("New", existing.getName());
            assertEquals("NewBrand", existing.getBrand());
            assertNotNull(existing.getUpdatedAt());
            assertNotNull(result);
        }

        @Test
        void givenPartialUpdate_thenOnlySpecifiedFieldsChanged() {
            Long id = 1L;
            DeviceEntity existing = new DeviceEntity();
            existing.setId(id);
            existing.setName("Old");
            existing.setBrand("OldBrand");
            existing.setState(DeviceState.AVAILABLE);

            DeviceUpdateDto dto = new DeviceUpdateDto();
            dto.setName("NewName");

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            DeviceResponseDto result = systemUnderTest.updateDevice(id, dto);

            assertEquals("NewName", existing.getName());
            assertEquals("OldBrand", existing.getBrand());
            assertNotNull(existing.getUpdatedAt());
        }
    }

      @Nested
    class GetByIdTests {

        private DeviceEntity existingDevice;

        @BeforeEach
        void init() {
            existingDevice = new DeviceEntity();
            existingDevice.setId(1L);
            existingDevice.setName("MacBook Pro");
            existingDevice.setBrand("Apple");
            existingDevice.setState(DeviceState.AVAILABLE);
            existingDevice.setCreatedAt(Instant.now());
            existingDevice.setUpdatedAt(Instant.now());
        }

        @Test
        void givenExistingDeviceId_whenGetById_thenReturnDeviceResponseDto() {
            when(repository.findById(1L)).thenReturn(Optional.of(existingDevice));

            Optional<DeviceResponseDto> result = systemUnderTest.getById(1L);

            assertThat(result).isPresent();
            DeviceResponseDto dto = result.get();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("MacBook Pro");
            assertThat(dto.getBrand()).isEqualTo("Apple");
            assertThat(dto.getState()).isEqualTo("AVAILABLE");

            verify(repository, times(1)).findById(1L);
        }

        @Test
        void givenNonExistingDeviceId_whenGetById_thenReturnEmptyOptional() {
            when(repository.findById(1000L)).thenReturn(Optional.empty());

            Optional<DeviceResponseDto> result = systemUnderTest.getById(1000L);

            assertThat(result).isEmpty();
            verify(repository, times(1)).findById(1000L);
        }
    }

    @Nested
    class GetAllDevicesTests {

        @Test
        void givenDevicesExist_whenGetAll_thenReturnPageOfDeviceResponseDto() {

            Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

            DeviceEntity device1 = new DeviceEntity();
            device1.setId(1L);
            device1.setName("Device A");
            device1.setBrand("Brand X");
            device1.setState(DeviceState.AVAILABLE);
            device1.setCreatedAt(Instant.now());
            device1.setUpdatedAt(Instant.now());

            DeviceEntity device2 = new DeviceEntity();
            device2.setId(2L);
            device2.setName("Device B");
            device2.setBrand("Brand Y");
            device2.setState(DeviceState.IN_USE);
            device2.setCreatedAt(Instant.now());
            device2.setUpdatedAt(Instant.now());

            Page<DeviceEntity> mockPage = new PageImpl<>(List.of(device1, device2), pageable, 2);

            when(repository.findAll(pageable)).thenReturn(mockPage);

            Page<DeviceResponseDto> result = systemUnderTest.getAllDevice(pageable);

            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent()).extracting("name")
                    .containsExactly("Device A", "Device B");

            verify(repository, times(1)).findAll(pageable);
        }

        @Test
        void givenNoDevicesExist_whenGetAll_thenReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 2);
            Page<DeviceEntity> mockPage = new PageImpl<>(List.of(), pageable, 0);

            when(repository.findAll(pageable)).thenReturn(mockPage);

            Page<DeviceResponseDto> result = systemUnderTest.getAllDevice(pageable);

            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getContent()).isEmpty();

            verify(repository, times(1)).findAll(pageable);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void givenExistingDevice_whenDelete_thenShouldSucceed() {
          
            DeviceEntity existingDevice = new DeviceEntity();
            existingDevice.setId(1L);
            existingDevice.setName("MacBook Pro");
            existingDevice.setBrand("Apple");
            existingDevice.setState(DeviceState.AVAILABLE);

            when(repository.findById(1L)).thenReturn(Optional.of(existingDevice));
       
            systemUnderTest.deleteDevice(1L);

            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).delete(existingDevice);
        }

        @Test
        void givenNonExistingDevice_whenDelete_thenThrowNotFoundException() {
            when(repository.findById(199L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> systemUnderTest.deleteDevice(199L))
                    .isInstanceOf(DeviceNotFoundException.class)
                    .hasMessageContaining("199");

            verify(repository, times(1)).findById(199L);
            verify(repository, never()).delete(Mockito.<DeviceEntity>any());
        }

        @Test
        void givenDeviceInUse_whenDelete_thenThrowOperationException() {
            DeviceEntity inUseDevice = new DeviceEntity();
            inUseDevice.setId(2L);
            inUseDevice.setName("Dell XPS");
            inUseDevice.setBrand("Dell");
            inUseDevice.setState(DeviceState.IN_USE);

            when(repository.findById(2L)).thenReturn(Optional.of(inUseDevice));

            assertThatThrownBy(() -> systemUnderTest.deleteDevice(2L))
                    .isInstanceOf(DeviceOperationException.class)
                    .hasMessageContaining("in use");

            verify(repository, times(1)).findById(2L);
            verify(repository, never()).delete(Mockito.<DeviceEntity>any());
        }
    }

    @Nested
    class FindDeviceTests {
        @Test
        void givenBrandAndState_whenFindDevices_thenReturnPageOfDtos() {

            String brand = "Apple";
            DeviceState state = DeviceState.AVAILABLE;
            Pageable pageable = PageRequest.of(0, 10);

            DeviceEntity device1 = new DeviceEntity();
            device1.setId(1L);
            device1.setName("MacBook Pro");
            device1.setBrand("Apple");
            device1.setState(DeviceState.AVAILABLE);

            DeviceEntity device2 = new DeviceEntity();
            device2.setId(2L);
            device2.setName("iPhone 14");
            device2.setBrand("Apple");
            device2.setState(DeviceState.AVAILABLE);

            Page<DeviceEntity> devicePage = new PageImpl<>(List.of(device1, device2));

            when(repository.findAll(ArgumentMatchers.<Specification<DeviceEntity>>any(), eq(pageable)))
                    .thenReturn(devicePage);

            Page<DeviceResponseDto> result = systemUnderTest.findDevices(brand, state, pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("MacBook Pro");
            assertThat(result.getContent().get(1).getName()).isEqualTo("iPhone 14");

            verify(repository, times(1))
                    .findAll(ArgumentMatchers.<Specification<DeviceEntity>>any(), eq(pageable));
        }

        @Test
        void givenBrandAndState_whenFindDevices_thenReturnEmpty() {
            String brand = "Apple";
            DeviceState state = DeviceState.AVAILABLE;
            Pageable pageable = PageRequest.of(0, 10);

            Page<DeviceEntity> emptyPage = new PageImpl<>(Collections.emptyList());

            when(repository.findAll(ArgumentMatchers.<Specification<DeviceEntity>>any(), eq(pageable)))
                    .thenReturn(emptyPage);

            Page<DeviceResponseDto> result = systemUnderTest.findDevices(brand, state, pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(repository, times(1))
                    .findAll(ArgumentMatchers.<Specification<DeviceEntity>>any(), eq(pageable));
        }

        @Test
        void givenBrandAndState_whenFindDevices_thenReturnDeviceDtos() {
            String brand = "Apple";
            DeviceState state = DeviceState.AVAILABLE;

            DeviceEntity device1 = new DeviceEntity();
            device1.setId(1L);
            device1.setName("MacBook Pro");
            device1.setBrand(brand);
            device1.setState(state);

            DeviceEntity device2 = new DeviceEntity();
            device2.setId(2L);
            device2.setName("MacBook Air");
            device2.setBrand(brand);
            device2.setState(state);

            List<DeviceEntity> devices = List.of(device1, device2);
            Page<DeviceEntity> devicePage = new PageImpl<>(devices);

            Pageable pageable = PageRequest.of(0, 10);

            when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(devicePage);

            Page<DeviceResponseDto> result = systemUnderTest.findDevices(brand, state, pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getId()).isEqualTo(device1.getId());
            assertThat(result.getContent().get(0).getName()).isEqualTo(device1.getName());
            assertThat(result.getContent().get(1).getId()).isEqualTo(device2.getId());
            assertThat(result.getContent().get(1).getName()).isEqualTo(device2.getName());

            verify(repository, times(1)).findAll(any(Specification.class), eq(pageable));
        }


    }

}