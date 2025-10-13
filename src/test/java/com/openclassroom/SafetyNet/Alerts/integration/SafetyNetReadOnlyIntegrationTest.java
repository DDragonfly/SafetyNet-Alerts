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
 * Tests d’intégration globaux sur l’application SafetyNet Alerts.
 * <p>
 * Vérifie que le chargement initial du fichier JSON fonctionne
 * et que les données principales (personnes, casernes, dossiers médicaux)
 * sont accessibles en mode lecture seule.
 */
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SafetyNetReadOnlyIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void firestation_integration_ok() throws Exception {
        mvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.adultCount").exists())
                .andExpect(jsonPath("$.childCount").exists())
                .andExpect(jsonPath("$.persons").isArray());
    }

    @Test
    void phoneAlert_integration_ok() throws Exception {
        mvc.perform(get("/phoneAlert").param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNumber());
    }
}
