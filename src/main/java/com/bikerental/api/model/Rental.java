package com.bikerental.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bicycle_id", nullable = false)
    private Bicycle bicycle;

    @NotBlank
    @Column(nullable = false)
    private String customerName;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer estimatedDurationHours;

    private LocalDateTime returnTime;

    @PositiveOrZero
    private Integer realUsedHours;

    @PositiveOrZero
    private Integer lateHours;

    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal baseCost;

    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal penaltyCost;

    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal totalCost;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    public Rental() {
    }

    public Rental(Bicycle bicycle, String customerName, LocalDateTime startTime,
                  Integer estimatedDurationHours, RentalStatus status) {
        this.bicycle = bicycle;
        this.customerName = customerName;
        this.startTime = startTime;
        this.estimatedDurationHours = estimatedDurationHours;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bicycle getBicycle() {
        return bicycle;
    }

    public void setBicycle(Bicycle bicycle) {
        this.bicycle = bicycle;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public LocalDateTime getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDateTime returnTime) {
        this.returnTime = returnTime;
    }

    public Integer getRealUsedHours() {
        return realUsedHours;
    }

    public void setRealUsedHours(Integer realUsedHours) {
        this.realUsedHours = realUsedHours;
    }

    public Integer getLateHours() {
        return lateHours;
    }

    public void setLateHours(Integer lateHours) {
        this.lateHours = lateHours;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public BigDecimal getPenaltyCost() {
        return penaltyCost;
    }

    public void setPenaltyCost(BigDecimal penaltyCost) {
        this.penaltyCost = penaltyCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}
