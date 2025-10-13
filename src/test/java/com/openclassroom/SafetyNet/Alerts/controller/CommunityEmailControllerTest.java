package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.service.CommunityEmailService;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CommunityEmailController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class CommunityEmailControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private CommunityEmailService communityEmailService;

    @Test
    void get_ok_returnsEmails() throws Exception {
        when(communityEmailService.getEmailsByCity("New York")).thenReturn(List.of("chandler.bing@gmail.com", "monica.geller@gmail.com")
        );

        mvc.perform(get("/communityEmail").param("city", "New York"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("chandler.bing@gmail.com"));
    }

    @Test
    void get_empty_whenNoResidents() throws Exception {
        when(communityEmailService.getEmailsByCity("Neverland")).thenReturn(List.of());

        mvc.perform(get("/communityEmail").param("city", "Neverland"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
