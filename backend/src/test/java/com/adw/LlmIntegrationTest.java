package com.adw;

import com.adw.ai.contract.DiagnosticAnalysis;
import com.adw.ai.orchestration.AutomotiveOrchestrator;
import com.adw.ai.provider.LlmClient;
import com.adw.ai.provider.LlmClientFactory;
import com.adw.diagnostic.DiagnosticSession;
import com.adw.vehicle.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class LlmIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("pgvector/pgvector:pg16");

    @MockitoBean
    LlmClientFactory clientFactory;

    @Autowired
    AutomotiveOrchestrator orchestrator;

    @Autowired
    MockMvc mockMvc;

    @Test
    void settingsRoundTripNeverExposesSecret() throws Exception {
        mockMvc.perform(put("/api/v1/settings/llm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"provider":"openai","model":"gpt-4o-mini","baseUrl":null,"enabled":true,"apiKey":"sk-super-secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasApiKey").value(true))
                .andExpect(jsonPath("$.provider").value("openai"));

        String body = mockMvc.perform(get("/api/v1/settings/llm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasApiKey").value(true))
                .andReturn().getResponse().getContentAsString();
        assertFalse(body.contains("sk-super-secret"));

        mockMvc.perform(delete("/api/v1/settings/llm/key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasApiKey").value(false));
    }

    @Test
    void orchestratorParsesValidAnalysis() {
        LlmClient client = clientReturning(VALID_JSON);
        when(clientFactory.activeClient()).thenReturn(Optional.of(client));
        DiagnosticAnalysis result = orchestrator.analyze(session(), "Código P0171");
        assertEquals("Posible fuga de aire no medido", result.summary());
        assertEquals(1, result.hypotheses().size());
        assertEquals(1, result.dtcs().size());
    }

    @Test
    void orchestratorRetriesOnceOnInvalidJson() {
        LlmClient client = clientReturning("no es json", VALID_JSON);
        when(clientFactory.activeClient()).thenReturn(Optional.of(client));
        DiagnosticAnalysis result = orchestrator.analyze(session(), "Código P0171");
        assertEquals("Posible fuga de aire no medido", result.summary());
    }

    @Test
    void orchestratorFailsWhenOutputAlwaysInvalid() {
        LlmClient client = clientReturning("no json", "tampoco json");
        when(clientFactory.activeClient()).thenReturn(Optional.of(client));
        assertThrows(RuntimeException.class, () -> orchestrator.analyze(session(), "Código P0171"));
    }

    private LlmClient clientReturning(String... responses) {
        LlmClient client = mock(LlmClient.class);
        when(client.generate(anyString(), anyString(), anyBoolean()))
                .thenReturn(responses[0], Arrays.copyOfRange(responses, 1, responses.length));
        return client;
    }

    private DiagnosticSession session() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake("Chevrolet");
        vehicle.setModel("Silverado");
        vehicle.setYear(2008);
        vehicle.setEngineDescription("5.3L V8");
        vehicle.setFuelType("gasoline");
        vehicle.setTransmission("automatic");

        DiagnosticSession session = new DiagnosticSession();
        session.setVehicle(vehicle);
        session.setComplaint("Ralentí inestable, consumo elevado y P0171");
        session.setOperatingConditions("En caliente, a ralentí");
        return session;
    }

    private static final String VALID_JSON = """
            {
              "summary": "Posible fuga de aire no medido",
              "vehicleContext": { "vehicleId": null, "configurationWarnings": [] },
              "knownFacts": [
                { "statement": "Código P0171 reportado", "origin": "USER", "sourceId": null }
              ],
              "missingInformation": [
                { "question": "¿El síntoma aparece en frío o caliente?", "reason": "Diferenciar causas" }
              ],
              "dtcs": [
                { "code": "P0171", "status": "ACTIVE", "description": "Sistema demasiado pobre (banco 1)", "sourceIds": [] }
              ],
              "hypotheses": [
                { "id": "H1", "title": "Fuga de admisión", "confidence": "MEDIUM",
                  "supportingEvidence": ["P0171"], "contradictingEvidence": [], "unknowns": [],
                  "status": "UNTESTED" }
              ],
              "testPlan": [
                { "id": "T1", "title": "Prueba de humo", "purpose": "Detectar fuga de admisión",
                  "tools": ["Smoke tester"], "preconditions": ["Motor apagado"], "procedure": [],
                  "expectedResults": [], "safetyNotes": [], "sourceIds": [], "status": "PENDING" }
              ],
              "safetyWarnings": [],
              "sources": [],
              "conclusionStatus": "PRELIMINARY"
            }
            """;
}
