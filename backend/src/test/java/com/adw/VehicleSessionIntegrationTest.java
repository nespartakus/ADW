package com.adw;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class VehicleSessionIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("pgvector/pgvector:pg16");

    @Autowired
    MockMvc mockMvc;

    @Test
    void registerVehicleCreateSessionAndReopen() throws Exception {
        String vehicleId = createVehicle();

        String sessionJson = mockMvc.perform(post("/api/v1/diagnostics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "vehicleId": "%s",
                                  "title": "Ralentí inestable",
                                  "complaint": "Consumo elevado y P0171"
                                }
                                """.formatted(vehicleId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.vehicle.make").value("Chevrolet"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String sessionId = JsonPath.read(sessionJson, "$.id");

        mockMvc.perform(get("/api/v1/diagnostics/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId))
                .andExpect(jsonPath("$.title").value("Ralentí inestable"));
    }

    @Test
    void softDeleteVehicleHidesIt() throws Exception {
        String vehicleId = createVehicle();

        mockMvc.perform(delete("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound());
    }

    private String createVehicle() throws Exception {
        String content = mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "make": "Chevrolet",
                                  "model": "Silverado",
                                  "year": 2008,
                                  "engineCode": "L59",
                                  "engineDescription": "5.3L V8",
                                  "fuelType": "gasoline",
                                  "transmission": "automatic"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(content, "$.id");
    }
}
