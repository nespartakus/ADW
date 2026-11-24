package com.adw.diagnostic;

import com.adw.diagnostic.dto.DiagnosticSessionRequest;
import com.adw.diagnostic.dto.DiagnosticSessionResponse;
import com.adw.shared.NotFoundException;
import com.adw.vehicle.Vehicle;
import com.adw.vehicle.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DiagnosticSessionService {

    private final DiagnosticSessionRepository repository;
    private final VehicleRepository vehicleRepository;

    public DiagnosticSessionService(
            DiagnosticSessionRepository repository,
            VehicleRepository vehicleRepository) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional(readOnly = true)
    public List<DiagnosticSessionResponse> list() {
        return repository.findAllWithVehicle().stream()
                .map(DiagnosticSessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiagnosticSessionResponse get(UUID id) {
        return DiagnosticSessionResponse.from(findEntity(id));
    }

    @Transactional
    public DiagnosticSessionResponse create(DiagnosticSessionRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado: " + request.vehicleId()));

        var session = new DiagnosticSession();
        session.setVehicle(vehicle);
        session.setTitle(request.title());
        session.setCustomerReference(request.customerReference());
        session.setComplaint(request.complaint());
        session.setOperatingConditions(request.operatingConditions());
        session.setStatus(SessionStatus.OPEN);
        session.setStartedAt(Instant.now());
        return DiagnosticSessionResponse.from(repository.save(session));
    }

    private DiagnosticSession findEntity(UUID id) {
        return repository.findWithVehicleById(id)
                .orElseThrow(() -> new NotFoundException("Diagnóstico no encontrado: " + id));
    }
}
