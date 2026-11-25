package com.adw.diagnostic;

import com.adw.diagnostic.dto.DiagnosticMessageResponse;
import com.adw.diagnostic.dto.DiagnosticSessionRequest;
import com.adw.diagnostic.dto.DiagnosticSessionResponse;
import com.adw.diagnostic.dto.SendMessageRequest;
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

    private final DiagnosticSessionService sessionService;
    private final DiagnosticMessageService messageService;

    public DiagnosticSessionController(
            DiagnosticSessionService sessionService,
            DiagnosticMessageService messageService) {
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    @GetMapping
    public List<DiagnosticSessionResponse> list() {
        return sessionService.list();
    }

    @GetMapping("/{id}")
    public DiagnosticSessionResponse get(@PathVariable UUID id) {
        return sessionService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosticSessionResponse create(@Valid @RequestBody DiagnosticSessionRequest request) {
        return sessionService.create(request);
    }

    @GetMapping("/{id}/messages")
    public List<DiagnosticMessageResponse> listMessages(@PathVariable UUID id) {
        return messageService.list(id);
    }

    @PostMapping("/{id}/messages")
    public List<DiagnosticMessageResponse> sendMessage(
            @PathVariable UUID id,
            @Valid @RequestBody SendMessageRequest request) {
        return messageService.send(id, request.content());
    }
}
