package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.service.FirestationCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = FirestationCrudController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class FirestationCrudControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FirestationCrudService firestationCrudService;

    @Test
    void post_created() throws Exception {
        when(firestationCrudService.create(any(Firestation.class))).thenReturn(true);

        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"748 Townings Dr","station":"4"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    void post_conflict_ifAlreadyExists() throws Exception {
        when(firestationCrudService.create(any(Firestation.class))).thenReturn(false);

        mvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"748 Townings Dr","station":"4"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void put_ok() throws Exception {
        when(firestationCrudService.updateStationForAddress("748 Townings Dr", "2")).thenReturn(true);

        mvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"748 Townings Dr","station":"2"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void put_notFound_ifAddressIsMissing() throws Exception {
        when(firestationCrudService.updateStationForAddress("unknown", "2")).thenReturn(false);

        mvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address":"unknown","station":"2"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_byAddress_noContent() throws Exception {
        when(firestationCrudService.deleteByAddress("748 Townings Dr")).thenReturn(true);

        mvc.perform(delete("/firestation").param("address", "748 Townings Dr"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void delete_byStation_noContent() throws Exception {
        when(firestationCrudService.deleteByStation("2")).thenReturn(true);

        mvc.perform(delete("/firestation").param("station", "2"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }
}
