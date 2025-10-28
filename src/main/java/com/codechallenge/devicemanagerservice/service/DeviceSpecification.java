package com.codechallenge.devicemanagerservice.service;

import com.codechallenge.devicemanagerservice.model.DeviceEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DeviceSpecification {

    public static Specification<DeviceEntity> searchBy(String brand, String state) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null && !brand.isBlank()) {
                predicates.add(builder.equal(root.get("brand"), brand));
            }
            if (state != null) {
                predicates.add(builder.equal(root.get("state"), state));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
