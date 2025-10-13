package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.ChildAlertDTO;
import com.openclassroom.SafetyNet.Alerts.dto.HouseholdMemberDTO;
import com.openclassroom.SafetyNet.Alerts.service.ChildAlertService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ChildAlertController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class ChildAlertControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    ChildAlertService childAlertService;

    @Test
    void get_withChildren_ok() throws Exception {
        // child
        ChildAlertDTO child = new ChildAlertDTO();
        child.setFirstName("Emma");
        child.setLastName("Geller-Green");
        child.setAge(8);

        // household members
        HouseholdMemberDTO m1 = new HouseholdMemberDTO();
        m1.setFirstName("Rachel");
        m1.setLastName("Green");

        HouseholdMemberDTO m2 = new HouseholdMemberDTO();
        m2.setFirstName("Ross");
        m2.setLastName("Geller");

        child.setHouseholdMembers(List.of(m1, m2));

        when(childAlertService.getChildrenByAddress(eq("12 Geller Green St"))).thenReturn(List.of(child));

        mvc.perform(get("/childAlert")
                        .param("address", "12 Geller Green St"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1)) // array de l'enfant
                .andExpect(jsonPath("$[0].firstName").value("Emma"))
                .andExpect(jsonPath("$[0].lastName").value("Geller-Green"))
                .andExpect(jsonPath("$[0].age").value(8))
                .andExpect(jsonPath("$[0].householdMembers.length()").value(2))
                .andExpect(jsonPath("$[0].householdMembers[0].firstName").value("Rachel"))
                .andExpect(jsonPath("$[0].householdMembers[0].lastName").value("Green"))
                .andExpect(jsonPath("$[0].householdMembers[1].firstName").value("Ross"))
                .andExpect(jsonPath("$[0].householdMembers[1].lastName").value("Geller"));
    }

    @Test
    void get_noChildren_returnsEmptyArray() throws Exception {
        when(childAlertService.getChildrenByAddress(eq("12 No Children St")))
                .thenReturn(List.of());

        mvc.perform(get("/childAlert").param("address", "12 No Children St"))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // chaine vide
    }

    @Test
    void get_missingParam_badRequest() throws Exception {
        mvc.perform(get("/childAlert"))
                .andExpect(status().isBadRequest());
    }
}
