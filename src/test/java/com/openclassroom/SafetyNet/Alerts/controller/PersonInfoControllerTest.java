package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.PersonInfoDTO;
import com.openclassroom.SafetyNet.Alerts.service.PersonInfoService;
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

/**
 * Test du contrôleur {@link com.openclassroom.SafetyNet.Alerts.controller.PersonInfoController}.
 * <p>
 * Vérifie que l’endpoint <code>/personInfo</code> renvoie les bonnes données
 * pour un nom donné et gère correctement les cas d’absence.
 */
@WebMvcTest(
        controllers = PersonInfoController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class PersonInfoControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PersonInfoService personInfoService;


    @Test
    void get_ok_multiplePersons() throws Exception {
        var p1 = new PersonInfoDTO();
        p1.setFirstName("Monica");
        p1.setLastName("Geller");
        p1.setAddress("12 Monica St");
        p1.setEmail("monica.geller@gmail.com");
        p1.setAge(30);
        p1.setMedications(List.of("aznol:350mg"));
        p1.setAllergies(List.of());

        var p2 = new PersonInfoDTO();
        p2.setFirstName("Ross");
        p2.setLastName("Geller");
        p2.setAddress("12 Ross St");
        p2.setEmail("ross.geller@gmail.com");
        p2.setAge(32);
        p2.setMedications(List.of());
        p2.setAllergies(List.of("peanut"));

        when(personInfoService.getPersonInfoByLastName("Geller")).thenReturn(List.of(p1, p2));

        mvc.perform(get("/personInfo").param("lastName", "Geller"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Monica"))
                .andExpect(jsonPath("$[1].firstName").value("Ross"));
    }

    @Test
    void get_emptyList_whenNoMatch() throws Exception {
        when(personInfoService.getPersonInfoByLastName("Unknown")).thenReturn(List.of());

        mvc.perform(get("/personInfo").param("lastName", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}
