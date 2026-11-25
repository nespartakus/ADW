package com.adw.settings;

import com.adw.ai.provider.LlmClientFactory;
import com.adw.ai.provider.LlmException;
import com.adw.infrastructure.SecretEncryptor;
import com.adw.settings.dto.LlmSettingsRequest;
import com.adw.settings.dto.LlmSettingsResponse;
import com.adw.settings.dto.TestConnectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LlmConfigurationService {

    private final LlmConfigurationRepository repository;
    private final SecretEncryptor encryptor;
    private final LlmClientFactory clientFactory;

    public LlmConfigurationService(
            LlmConfigurationRepository repository,
            SecretEncryptor encryptor,
            LlmClientFactory clientFactory) {
        this.repository = repository;
        this.encryptor = encryptor;
        this.clientFactory = clientFactory;
    }

    @Transactional(readOnly = true)
    public LlmSettingsResponse get() {
        return repository.findFirstByOrderByUpdatedAtDesc()
                .map(this::toResponse)
                .orElseGet(LlmSettingsResponse::empty);
    }

    @Transactional
    public LlmSettingsResponse update(LlmSettingsRequest request) {
        var config = repository.findFirstByOrderByUpdatedAtDesc().orElseGet(LlmConfiguration::new);
        config.setProvider(request.provider().trim());
        config.setModel(request.model().trim());
        config.setBaseUrl(request.baseUrl());
        config.setEnabled(request.enabled());
        if (request.apiKey() != null && !request.apiKey().isBlank()) {
            config.setEncryptedApiKey(encryptor.encrypt(request.apiKey()));
        }
        config.setUpdatedAt(Instant.now());
        return toResponse(repository.save(config));
    }

    @Transactional
    public LlmSettingsResponse deleteKey() {
        var config = repository.findFirstByOrderByUpdatedAtDesc().orElseGet(LlmConfiguration::new);
        config.setEncryptedApiKey(null);
        config.setUpdatedAt(Instant.now());
        return toResponse(repository.save(config));
    }

    @Transactional(readOnly = true)
    public TestConnectionResponse test() {
        var client = clientFactory.activeClient().orElseThrow(
                () -> new LlmException(LlmException.Kind.NOT_CONFIGURED,
                        "No hay proveedor LLM configurado o habilitado"));
        try {
            String response = client.generate(
                    "Eres un asistente técnico. Responde exactamente con la palabra OK.",
                    "Prueba de conexión", false);
            return new TestConnectionResponse(true, client.describe(), response.trim());
        } catch (LlmException e) {
            return new TestConnectionResponse(false, null, e.getMessage());
        }
    }

    private LlmSettingsResponse toResponse(LlmConfiguration c) {
        boolean hasKey = c.getEncryptedApiKey() != null && !c.getEncryptedApiKey().isBlank();
        return new LlmSettingsResponse(c.getProvider(), c.getModel(), c.getBaseUrl(), c.isEnabled(), hasKey);
    }
}
