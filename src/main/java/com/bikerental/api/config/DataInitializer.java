package com.bikerental.api.config;

import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import com.bikerental.api.repository.BicycleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BicycleRepository bicycleRepository;

    public DataInitializer(BicycleRepository bicycleRepository) {
        this.bicycleRepository = bicycleRepository;
    }

    @Override
    public void run(String... args) {
        createBicycleIfMissing("BIC-001", BicycleType.URBANA, BicycleStatus.DISPONIBLE);
        createBicycleIfMissing("BIC-002", BicycleType.MONTANA, BicycleStatus.DISPONIBLE);
        createBicycleIfMissing("BIC-003", BicycleType.ELECTRICA, BicycleStatus.DISPONIBLE);
        createBicycleIfMissing("BIC-004", BicycleType.MONTANA, BicycleStatus.EN_MANTENIMIENTO);
        createBicycleIfMissing("BIC-005", BicycleType.URBANA, BicycleStatus.DISPONIBLE);
    }

    private void createBicycleIfMissing(String code, BicycleType type, BicycleStatus status) {
        if (!bicycleRepository.existsByCode(code)) {
            bicycleRepository.save(new Bicycle(code, type, status));
        }
    }
}
