package com.bikerental.api.repository;

import com.bikerental.api.model.Rental;
import com.bikerental.api.model.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByBicycleCodeOrderByStartTimeDesc(String code);

    List<Rental> findByStatus(RentalStatus status);
}
