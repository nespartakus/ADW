package com.adw.diagnostic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosticMessageRepository extends JpaRepository<DiagnosticMessage, UUID> {

    List<DiagnosticMessage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
