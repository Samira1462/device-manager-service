package com.codechallenge.devicemanagerservice.repository;

import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> , JpaSpecificationExecutor<DeviceEntity> {}
