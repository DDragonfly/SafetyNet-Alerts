package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.FireAddressDTO;
import com.openclassroom.SafetyNet.Alerts.dto.ResidentAtAddressDTO;
import com.openclassroom.SafetyNet.Alerts.service.FireAddressService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = FireAddressController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)
public class FireAddressControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private FireAddressService fireAddressService;

    @Test
    void get_ok_returnHousehold() throws Exception {
        ResidentAtAddressDTO r1 = new ResidentAtAddressDTO();
        r1.setFirstName("Monica");
        r1.setLastName("Geller");
        r1.setPhone("111-111-1111");
        r1.setAge(34);
        r1.setMedications(List.of("aznol:350mg"));
        r1.setAllergies(List.of("nillacilan"));

        ResidentAtAddressDTO r2 = new ResidentAtAddressDTO();
        r2.setFirstName("Chandler");
        r2.setLastName("Bing");
        r2.setPhone("222-222-2222");
        r2.setAge(35);
        r2.setMedications(List.of());
        r2.setAllergies(List.of());

        FireAddressDTO dto = new FireAddressDTO();
        dto.setStationNumber("3");
        dto.setResidents(List.of(r1, r2));

        when(fireAddressService.getFireInfoByAddress(eq("12 Chandler St"))).thenReturn(dto);

        mvc.perform(get("/fire").param("address", "12 Chandler St"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stationNumber").value("3"))
                .andExpect(jsonPath("$.residents.length()").value(2))
                .andExpect(jsonPath("$.residents[0].firstName").value("Monica"))
                .andExpect(jsonPath("$.residents[0].lastName").value("Geller"))
                .andExpect(jsonPath("$.residents[0].phone").value("111-111-1111"))
                .andExpect(jsonPath("$.residents[0].age").value(34))
                .andExpect(jsonPath("$.residents[0].medications[0]").value("aznol:350mg"))
                .andExpect(jsonPath("$.residents[0].allergies[0]").value("nillacilan"))
                .andExpect(jsonPath("$.residents[1].firstName").value("Chandler"))
                .andExpect(jsonPath("$.residents[1].lastName").value("Bing"))
                .andExpect(jsonPath("$.residents[1].phone").value("222-222-2222"))
                .andExpect(jsonPath("$.residents[1].age").value(35));
    }

    @Test
    void getFireInfo_notFound_returnsEmptyJson() throws Exception {

        when(fireAddressService.getFireInfoByAddress(eq("Unknown Address"))).thenReturn(new FireAddressDTO());

        mvc.perform(get("/fire").param("address", "Unknown Address"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void get_badRequest_whenMissingParam() throws Exception {
        mvc.perform(get("/fire"))
                .andExpect(status().isBadRequest());
    }

}
