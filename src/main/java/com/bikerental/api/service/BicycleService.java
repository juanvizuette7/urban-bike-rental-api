package com.bikerental.api.service;

import com.bikerental.api.dto.BicycleRequestDTO;
import com.bikerental.api.dto.BicycleResponseDTO;
import com.bikerental.api.exception.DuplicateResourceException;
import com.bikerental.api.exception.ResourceNotFoundException;
import com.bikerental.api.model.Bicycle;
import com.bikerental.api.model.BicycleStatus;
import com.bikerental.api.model.BicycleType;
import com.bikerental.api.repository.BicycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BicycleService {

    private final BicycleRepository bicycleRepository;

    public BicycleService(BicycleRepository bicycleRepository) {
        this.bicycleRepository = bicycleRepository;
    }

    @Transactional
    public BicycleResponseDTO create(BicycleRequestDTO request) {
        if (bicycleRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Ya existe una bicicleta con el codigo: " + request.code());
        }

        Bicycle bicycle = new Bicycle(request.code(), request.type(), request.status());
        Bicycle savedBicycle = bicycleRepository.save(bicycle);

        return toResponse(savedBicycle);
    }

    @Transactional(readOnly = true)
    public List<BicycleResponseDTO> findAll() {
        return bicycleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BicycleResponseDTO> findAvailable(BicycleType type) {
        List<Bicycle> bicycles = type == null
                ? bicycleRepository.findByStatus(BicycleStatus.DISPONIBLE)
                : bicycleRepository.findByStatusAndType(BicycleStatus.DISPONIBLE, type);

        return bicycles.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Bicycle findByCodeOrThrow(String code) {
        return bicycleRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una bicicleta con el codigo: " + code));
    }

    private BicycleResponseDTO toResponse(Bicycle bicycle) {
        return new BicycleResponseDTO(
                bicycle.getId(),
                bicycle.getCode(),
                bicycle.getType(),
                bicycle.getStatus()
        );
    }
}
