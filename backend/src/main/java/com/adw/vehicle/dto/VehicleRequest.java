package com.adw.vehicle.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VehicleRequest(
        @Size(max = 17) String vin,
        @NotBlank @Size(max = 100) String make,
        @NotBlank @Size(max = 100) String model,
        @NotNull @Min(1900) @Max(2100) Integer year,
        @Size(max = 50) String market,
        @Size(max = 100) String platform,
        @Size(max = 100) String engineCode,
        @Size(max = 255) String engineDescription,
        @DecimalMin("0.0") @DecimalMax("9999.99") @Digits(integer = 4, fraction = 2) BigDecimal displacement,
        @Size(max = 50) String fuelType,
        @Size(max = 50) String aspiration,
        @Size(max = 100) String ecu,
        @Size(max = 100) String transmission,
        @Size(max = 4000) String modifications,
        @Size(max = 4000) String notes
) {
}
