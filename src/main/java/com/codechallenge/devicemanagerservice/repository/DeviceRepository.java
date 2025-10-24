package com.codechallenge.devicemanagerservice.repository;

import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import com.codechallenge.devicemanagerservice.model.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {

    List<DeviceEntity> findByBrand(String brand);

    List<DeviceEntity> findByState(DeviceState state);
}
