package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.PersonCoveredDTO;
import com.openclassroom.SafetyNet.Alerts.service.PhoneAlertService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test du contrôleur {@link com.openclassroom.SafetyNet.Alerts.controller.PhoneAlertController}.
 * <p>
 * Vérifie le retour des numéros de téléphone pour une caserne donnée.
 */
@WebMvcTest(
        controllers = PhoneAlertController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class PhoneAlertControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    PhoneAlertService phoneAlertService;

    @Test
    void get_ok_returnList() throws Exception {
        when(phoneAlertService.getPhoneByStation(eq("1"))).thenReturn(List.of("111-111-1111","222-222-2222", "333-333-3333"));

        mvc.perform(get("/phoneAlert").param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("111-111-1111"))
                .andExpect(jsonPath("$[1]").value("222-222-2222"))
                .andExpect(jsonPath("$[2]").value("333-333-3333"));
    }

    @Test
    void getPhones_empty_returnsEmptyArray() throws Exception {
        when(phoneAlertService.getPhoneByStation(eq("99"))).thenReturn(List.of());
        mvc.perform(get("/phoneAlert").param("firestation", "99"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }
}
