package com.adw.vehicle.dto;

import com.adw.vehicle.Vehicle;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String vin,
        String make,
        String model,
        Integer year,
        String market,
        String platform,
        String engineCode,
        String engineDescription,
        BigDecimal displacement,
        String fuelType,
        String aspiration,
        String ecu,
        String transmission,
        String modifications,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {

    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(
                v.getId(),
                v.getVin(),
                v.getMake(),
                v.getModel(),
                v.getYear(),
                v.getMarket(),
                v.getPlatform(),
                v.getEngineCode(),
                v.getEngineDescription(),
                v.getDisplacement(),
                v.getFuelType(),
                v.getAspiration(),
                v.getEcu(),
                v.getTransmission(),
                v.getModifications(),
                v.getNotes(),
                v.getCreatedAt(),
                v.getUpdatedAt());
    }
}
