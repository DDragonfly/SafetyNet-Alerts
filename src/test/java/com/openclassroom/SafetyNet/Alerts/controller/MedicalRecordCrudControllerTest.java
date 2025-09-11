package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.service.MedicalRecordCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MedicalRecordCrudController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class MedicalRecordCrudControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MedicalRecordCrudService medicalRecordCrudService;

    @Test
    void post_created() throws Exception {
        when(medicalRecordCrudService.create(any(MedicalRecord.class))).thenReturn(true);

        mvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Monica","lastName":"Geller","birthdate":"03/06/1984","medications":["aznol:350mg"],"allergies":["nillacilan"]}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void post_conflict_ifExists() throws Exception {
        when(medicalRecordCrudService.create(any(MedicalRecord.class))).thenReturn(false);

        mvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Monica","lastName":"Geller","birthdate":"03/06/1984"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void put_ok() throws Exception {
        when(medicalRecordCrudService.update(eq("Monica"), eq("Geller"), any(MedicalRecord.class))).thenReturn(true);

        mvc.perform(put("/medicalRecord?firstName=Monica&lastName=Geller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"birthdate":"03/06/1984","medications":["newmed:10mg"],"allergies": []}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void delete_noContent() throws Exception {
        when(medicalRecordCrudService.delete("Monica", "Geller")).thenReturn(true);

        mvc.perform(delete("/medicalRecord?firstName=Monica&lastName=Geller"))
                .andExpect(status().isNoContent());
    }

}
