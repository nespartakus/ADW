package com.adw.vehicle;

import com.adw.shared.NotFoundException;
import com.adw.vehicle.dto.VehicleRequest;
import com.adw.vehicle.dto.VehicleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(VehicleResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse get(UUID id) {
        return VehicleResponse.from(findEntity(id));
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        var vehicle = new Vehicle();
        apply(vehicle, request);
        return VehicleResponse.from(repository.save(vehicle));
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        var vehicle = findEntity(id);
        apply(vehicle, request);
        return VehicleResponse.from(repository.save(vehicle));
    }

    @Transactional
    public void delete(UUID id) {
        findEntity(id).softDelete();
    }

    private Vehicle findEntity(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado: " + id));
    }

    private void apply(Vehicle vehicle, VehicleRequest r) {
        vehicle.setVin(r.vin());
        vehicle.setMake(r.make());
        vehicle.setModel(r.model());
        vehicle.setYear(r.year());
        vehicle.setMarket(r.market());
        vehicle.setPlatform(r.platform());
        vehicle.setEngineCode(r.engineCode());
        vehicle.setEngineDescription(r.engineDescription());
        vehicle.setDisplacement(r.displacement());
        vehicle.setFuelType(r.fuelType());
        vehicle.setAspiration(r.aspiration());
        vehicle.setEcu(r.ecu());
        vehicle.setTransmission(r.transmission());
        vehicle.setModifications(r.modifications());
        vehicle.setNotes(r.notes());
    }
}
