package com.adw.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LlmSettingsRequest(
        @NotBlank @Size(max = 50) String provider,
        @NotBlank @Size(max = 100) String model,
        @Size(max = 500) String baseUrl,
        boolean enabled,
        @Size(max = 500) String apiKey
) {
}
