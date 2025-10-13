package com.openclassroom.SafetyNet.Alerts.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test d’intégration du contrôleur {@link com.openclassroom.SafetyNet.Alerts.controller.FirestationController}.
 * <p>
 * Vérifie le bon fonctionnement de l’endpoint <code>/firestation?stationNumber={n}</code>
 * via {@link org.springframework.test.web.servlet.MockMvc}.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class FireStationControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void getCoverage_existingStation_returns200_andJsonShape() throws Exception {
        mvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.adultCount").exists())
                .andExpect(jsonPath("$.childCount").exists())
                .andExpect(jsonPath("$.persons").isArray())
                .andExpect(jsonPath("$.persons.length()").value(6));
    }

    @Test
    void getCoverage_unknownStation_returnsEmptyPayload() throws Exception {
        mvc.perform(get("/firestation").param("stationNumber", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultCount").value(0))
                .andExpect(jsonPath("$.childCount").value(0))
                .andExpect(jsonPath("$.persons.length()").value(0));
    }
}
