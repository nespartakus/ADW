package com.adw.settings;

import com.adw.settings.dto.LlmSettingsRequest;
import com.adw.settings.dto.LlmSettingsResponse;
import com.adw.settings.dto.TestConnectionResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings/llm")
public class LlmConfigurationController {

    private final LlmConfigurationService service;

    public LlmConfigurationController(LlmConfigurationService service) {
        this.service = service;
    }

    @GetMapping
    public LlmSettingsResponse get() {
        return service.get();
    }

    @PutMapping
    public LlmSettingsResponse update(@Valid @RequestBody LlmSettingsRequest request) {
        return service.update(request);
    }

    @PostMapping("/test")
    public TestConnectionResponse test() {
        return service.test();
    }

    @DeleteMapping("/key")
    public LlmSettingsResponse deleteKey() {
        return service.deleteKey();
    }
}
