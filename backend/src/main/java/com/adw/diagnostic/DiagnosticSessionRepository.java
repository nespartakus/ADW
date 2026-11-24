package com.adw.diagnostic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiagnosticSessionRepository extends JpaRepository<DiagnosticSession, UUID> {

    @Query("select s from DiagnosticSession s join fetch s.vehicle order by s.startedAt desc")
    List<DiagnosticSession> findAllWithVehicle();

    @Query("select s from DiagnosticSession s join fetch s.vehicle where s.id = :id")
    Optional<DiagnosticSession> findWithVehicleById(@Param("id") UUID id);
}
