package com.adw.ai.provider;

import com.adw.infrastructure.SecretEncryptor;
import com.adw.settings.LlmConfiguration;
import com.adw.settings.LlmConfigurationRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Builds a configured {@link LlmClient} from the stored provider settings.
 * A client is built per request for simplicity; caching can be added later.
 */
@Component
public class LlmClientFactory {

    private final LlmConfigurationRepository repository;
    private final SecretEncryptor encryptor;

    public LlmClientFactory(LlmConfigurationRepository repository, SecretEncryptor encryptor) {
        this.repository = repository;
        this.encryptor = encryptor;
    }

    public Optional<LlmClient> activeClient() {
        Optional<LlmConfiguration> config = repository.findFirstByEnabledTrueOrderByUpdatedAtDesc();
        if (config.isEmpty()) {
            return Optional.empty();
        }
        LlmConfiguration c = config.get();
        String apiKey = encryptor.decrypt(c.getEncryptedApiKey());
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(build(c, apiKey));
    }

    private LlmClient build(LlmConfiguration c, String apiKey) {
        if ("openai".equalsIgnoreCase(c.getProvider())) {
            return new OpenAiLlmClient(apiKey, c.getModel(), c.getBaseUrl(), Duration.ofSeconds(60));
        }
        throw new LlmException(LlmException.Kind.PROVIDER_ERROR,
                "Proveedor no soportado: " + c.getProvider());
    }
}
