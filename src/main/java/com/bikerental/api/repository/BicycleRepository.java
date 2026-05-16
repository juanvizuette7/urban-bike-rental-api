package com.bikerental.api.repository;

import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BicycleRepository extends JpaRepository<Bicycle, Long> {

    Optional<Bicycle> findByCode(String code);

    boolean existsByCode(String code);

    List<Bicycle> findByStatus(BicycleStatus status);

    List<Bicycle> findByStatusAndType(BicycleStatus status, BicycleType type);
}
