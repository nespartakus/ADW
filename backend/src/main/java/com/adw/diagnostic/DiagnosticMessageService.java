package com.adw.diagnostic;

import com.adw.ai.contract.DiagnosticAnalysis;
import com.adw.ai.orchestration.AutomotiveOrchestrator;
import com.adw.diagnostic.dto.DiagnosticMessageResponse;
import com.adw.shared.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DiagnosticMessageService {

    private final DiagnosticMessageRepository messageRepository;
    private final DiagnosticSessionRepository sessionRepository;
    private final AutomotiveOrchestrator orchestrator;
    private final ObjectMapper objectMapper;

    public DiagnosticMessageService(
            DiagnosticMessageRepository messageRepository,
            DiagnosticSessionRepository sessionRepository,
            AutomotiveOrchestrator orchestrator,
            ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
        this.orchestrator = orchestrator;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<DiagnosticMessageResponse> list(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<DiagnosticMessageResponse> send(UUID sessionId, String content) {
        DiagnosticSession session = sessionRepository.findWithVehicleById(sessionId)
                .orElseThrow(() -> new NotFoundException("Diagnóstico no encontrado: " + sessionId));

        DiagnosticMessage userMessage = new DiagnosticMessage();
        userMessage.setSession(session);
        userMessage.setRole(MessageRole.USER);
        userMessage.setContent(content);
        userMessage.setCreatedAt(Instant.now());
        messageRepository.save(userMessage);

        DiagnosticAnalysis analysis = orchestrator.analyze(session, content);

        DiagnosticMessage assistantMessage = new DiagnosticMessage();
        assistantMessage.setSession(session);
        assistantMessage.setRole(MessageRole.ASSISTANT);
        assistantMessage.setContent(analysis.summary());
        assistantMessage.setStructuredPayload(writeJson(analysis));
        assistantMessage.setCreatedAt(Instant.now());
        messageRepository.save(assistantMessage);

        return List.of(toResponse(userMessage), toResponse(assistantMessage));
    }

    private DiagnosticMessageResponse toResponse(DiagnosticMessage m) {
        return new DiagnosticMessageResponse(
                m.getId(),
                m.getRole(),
                m.getContent(),
                parseJson(m.getStructuredPayload()),
                m.getCreatedAt());
    }

    private String writeJson(DiagnosticAnalysis analysis) {
        try {
            return objectMapper.writeValueAsString(analysis);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize analysis", e);
        }
    }

    private tools.jackson.databind.JsonNode parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }
}
