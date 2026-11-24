package com.adw.diagnostic.dto;

import com.adw.diagnostic.DiagnosticSession;
import com.adw.diagnostic.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public record DiagnosticSessionResponse(
        UUID id,
        VehicleSummary vehicle,
        String title,
        String customerReference,
        String complaint,
        String operatingConditions,
        SessionStatus status,
        Instant startedAt,
        Instant closedAt
) {

    public record VehicleSummary(UUID id, String make, String model, Integer year) {
    }

    public static DiagnosticSessionResponse from(DiagnosticSession s) {
        var v = s.getVehicle();
        return new DiagnosticSessionResponse(
                s.getId(),
                new VehicleSummary(v.getId(), v.getMake(), v.getModel(), v.getYear()),
                s.getTitle(),
                s.getCustomerReference(),
                s.getComplaint(),
                s.getOperatingConditions(),
                s.getStatus(),
                s.getStartedAt(),
                s.getClosedAt());
    }
}
