package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.FirestationCoverageDTO;
import com.openclassroom.SafetyNet.Alerts.dto.PersonCoveredDTO;
import com.openclassroom.SafetyNet.Alerts.service.FirestationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = FirestationController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class FirestationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    FirestationService firestationService;

    @Test
    void getCoverage_ok() throws Exception {

        PersonCoveredDTO p1 = new PersonCoveredDTO();
        p1.setFirstName("Chandler");
        p1.setLastName("Bing");
        p1.setAddress("12 Chandler St");
        p1.setPhone("123-456-7890");

        PersonCoveredDTO p2 = new PersonCoveredDTO();
        p2.setFirstName("Monica");
        p2.setLastName("Geller");
        p2.setAddress("34 Monica St");
        p2.setPhone("154-955-8546");

        FirestationCoverageDTO dto = new FirestationCoverageDTO();
        dto.setPersons(List.of(p1,p2));
        dto.setAdultCount(1);
        dto.setChildCount(1);

        when(firestationService.getCoverageByStationNumber("1")).thenReturn(dto);

        mvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persons.length()").value(2))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(1))
                .andExpect(jsonPath("$.persons[0].firstName").value("Chandler"))
                .andExpect(jsonPath("$.persons[0].lastName").value("Bing"))
                .andExpect(jsonPath("$.persons[0].address").value("12 Chandler St"))
                .andExpect(jsonPath("$.persons[0].phone").value("123-456-7890"));
    }

    @Test
    void getCoverage_empty_whenNoAddresses() throws Exception {
        FirestationCoverageDTO empty = new FirestationCoverageDTO();
        empty.setPersons(List.of());
        empty.setAdultCount(0);
        empty.setChildCount(0);

        when(firestationService.getCoverageByStationNumber("99")).thenReturn(empty);

        mvc.perform(get("/firestation").param("stationNumber", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons.length()").value(0))
                .andExpect(jsonPath("$.adultCount").value(0))
                .andExpect(jsonPath("$.childCount").value(0));
    }

    @Test
    void getCoverage_badRequest_whenMissingParam() throws Exception {
        mvc.perform(get("/firestation"))
                .andExpect(status().isBadRequest());
    }
}
