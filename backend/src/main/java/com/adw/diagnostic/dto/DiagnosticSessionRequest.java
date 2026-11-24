package com.adw.diagnostic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DiagnosticSessionRequest(
        @NotNull UUID vehicleId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 200) String customerReference,
        @Size(max = 8000) String complaint,
        @Size(max = 8000) String operatingConditions
) {
}
