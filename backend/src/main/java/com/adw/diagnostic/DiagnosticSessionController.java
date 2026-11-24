package com.adw.diagnostic;

import com.adw.diagnostic.dto.DiagnosticSessionRequest;
import com.adw.diagnostic.dto.DiagnosticSessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/diagnostics")
public class DiagnosticSessionController {

    private final DiagnosticSessionService service;

    public DiagnosticSessionController(DiagnosticSessionService service) {
        this.service = service;
    }

    @GetMapping
    public List<DiagnosticSessionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public DiagnosticSessionResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosticSessionResponse create(@Valid @RequestBody DiagnosticSessionRequest request) {
        return service.create(request);
    }
}
