package com.adw.diagnostic.dto;

import com.adw.diagnostic.MessageRole;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record DiagnosticMessageResponse(
        UUID id,
        MessageRole role,
        String content,
        JsonNode structuredPayload,
        Instant createdAt
) {
}
