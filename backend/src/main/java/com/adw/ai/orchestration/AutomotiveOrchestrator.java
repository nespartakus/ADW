package com.adw.ai.orchestration;

import com.adw.ai.contract.DiagnosticAnalysis;
import com.adw.ai.provider.LlmClient;
import com.adw.ai.provider.LlmClientFactory;
import com.adw.ai.provider.LlmException;
import com.adw.diagnostic.DiagnosticSession;
import com.adw.vehicle.Vehicle;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Initial automotive orchestrator: builds a guarded prompt, requests structured
 * JSON from the provider, validates it against the contract, and performs one
 * limited repair attempt on invalid output.
 */
@Component
public class AutomotiveOrchestrator {

    private final LlmClientFactory clientFactory;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public AutomotiveOrchestrator(
            LlmClientFactory clientFactory,
            ObjectMapper objectMapper,
            Validator validator) {
        this.clientFactory = clientFactory;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    public DiagnosticAnalysis analyze(DiagnosticSession session, String userMessage) {
        LlmClient client = clientFactory.activeClient()
                .orElseThrow(() -> new LlmException(
                        LlmException.Kind.NOT_CONFIGURED,
                        "No hay proveedor LLM configurado o habilitado"));

        String system = systemPrompt();
        String user = buildUserPrompt(session, userMessage);

        try {
            return generateAndValidate(client, system, user);
        } catch (LlmException e) {
            throw e;
        } catch (AnalysisException e) {
            return generateAndValidate(client, system, user + "\n\n" + repairInstruction(e.getMessage()));
        }
    }

    private DiagnosticAnalysis generateAndValidate(LlmClient client, String system, String user) {
        String raw = client.generate(system, user, true);
        String json = extractJson(raw);
        try {
            DiagnosticAnalysis analysis = objectMapper.readValue(json, DiagnosticAnalysis.class);
            var violations = validator.validate(analysis);
            if (!violations.isEmpty()) {
                throw new AnalysisException("La salida no cumple el contrato: " + violations);
            }
            return analysis;
        } catch (AnalysisException e) {
            throw e;
        } catch (Exception e) {
            throw new AnalysisException("No se pudo interpretar el JSON del modelo: " + e.getMessage());
        }
    }

    private String extractJson(String raw) {
        String s = raw == null ? "" : raw.trim();
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl >= 0) {
                s = s.substring(nl + 1);
                int end = s.lastIndexOf("```");
                if (end >= 0) {
                    s = s.substring(0, end);
                }
            }
        }
        s = s.trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start >= 0 && end > start) {
            s = s.substring(start, end + 1);
        }
        return s;
    }

    private String buildUserPrompt(DiagnosticSession session, String userMessage) {
        Vehicle v = session.getVehicle();
        StringBuilder sb = new StringBuilder();
        sb.append("Vehículo: ").append(v.getMake()).append(' ').append(v.getModel())
                .append(' ').append(v.getYear()).append('\n');
        appendIfPresent(sb, "Motor", firstNonBlank(v.getEngineDescription(), v.getEngineCode()));
        appendIfPresent(sb, "Combustible", v.getFuelType());
        appendIfPresent(sb, "Transmisión", v.getTransmission());
        appendIfPresent(sb, "Modificaciones", v.getModifications());
        appendIfPresent(sb, "Motivo de ingreso", session.getComplaint());
        appendIfPresent(sb, "Condiciones", session.getOperatingConditions());
        sb.append("\nMensaje del técnico:\n").append(userMessage);
        return sb.toString();
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    private static void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(label).append(": ").append(value).append('\n');
        }
    }

    private String repairInstruction(String reason) {
        return "La respuesta anterior no fue JSON válido según el contrato (motivo: " + reason
                + "). Responde ÚNICAMENTE con un objeto JSON que cumpla exactamente el esquema indicado, "
                + "sin texto adicional ni bloques de código.";
    }

    private String systemPrompt() {
        return """
                Eres un asistente técnico de diagnóstico automotriz para un taller profesional.
                Trabajas con técnicos expertos. No eres un chatbot genérico.

                PRINCIPIO CENTRAL: la IA propone; el técnico verifica. Nunca presentes una hipótesis
                como un diagnóstico confirmado.

                REGLAS OBLIGATORIAS:
                - Distingue siempre: hechos aportados por el usuario, datos medidos, datos de
                  documentación, inferencias tuyas e hipótesis pendientes de comprobar.
                - NO inventes valores de torque, presión, resistencia, voltaje, pinouts, boletines,
                  procedimientos ni especificaciones OEM. Cada dato técnico específico debe tener
                  fuente, vehículo/variante aplicable y unidad.
                - Si falta contexto crítico, pídelo (missingInformation) en lugar de asumir.
                - Genera hipótesis alternativas ordenadas, no una conclusión inmediata.
                - Propón pruebas discriminantes, seguras, con herramienta, condición y resultado
                  esperado SOLO cuando exista una fuente aplicable.
                - Incluye advertencias de seguridad relevantes (superficies calientes, ventiladores,
                  combustible, alta tensión, motor en marcha).
                - No recomiendes puentear terminales, cortocircuitos ni manipular airbags/pretensores.
                - Si no hay suficiente información para afirmar algo, usa confidence LOW y decláralo.

                DEBES responder ÚNICAMENTE con un objeto JSON válido (sin texto adicional, sin bloques
                de código) que cumpla esta estructura exacta:

                {
                  "summary": "Resumen breve del problema",
                  "vehicleContext": { "vehicleId": "uuid o null", "configurationWarnings": [] },
                  "knownFacts": [ { "statement": "...", "origin": "USER|DEVICE|DOCUMENT", "sourceId": null } ],
                  "missingInformation": [ { "question": "...", "reason": "..." } ],
                  "dtcs": [ { "code": "P0171", "status": "ACTIVE|PENDING|HISTORICAL", "description": "...", "sourceIds": [] } ],
                  "hypotheses": [
                    { "id": "H1", "title": "...", "confidence": "LOW|MEDIUM|HIGH",
                      "supportingEvidence": [], "contradictingEvidence": [], "unknowns": [],
                      "status": "UNTESTED|SUPPORTED|REFUTED|CONFIRMED" }
                  ],
                  "testPlan": [
                    { "id": "T1", "title": "...", "purpose": "...", "tools": [],
                      "preconditions": [], "procedure": [], "expectedResults": [],
                      "safetyNotes": [], "sourceIds": [], "status": "PENDING|IN_PROGRESS|PASSED|FAILED|INCONCLUSIVE" }
                  ],
                  "safetyWarnings": [],
                  "sources": [ { "id": "SRC1", "documentTitle": "...", "publisher": "...",
                                "page": "...", "url": null, "excerpt": "..." } ],
                  "conclusionStatus": "PRELIMINARY|CONFIRMED|UNCONFIRMED"
                }

                Los niveles de confianza son estimaciones cualitativas, no probabilidades calibradas.
                Responde en español (México).
                """;
    }

    private static class AnalysisException extends RuntimeException {
        AnalysisException(String message) {
            super(message);
        }
    }
}
