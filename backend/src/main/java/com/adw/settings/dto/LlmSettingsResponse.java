package com.adw.settings.dto;

public record LlmSettingsResponse(
        String provider,
        String model,
        String baseUrl,
        boolean enabled,
        boolean hasApiKey
) {

    public static LlmSettingsResponse empty() {
        return new LlmSettingsResponse(null, null, null, false, false);
    }
}
