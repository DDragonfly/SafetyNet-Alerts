package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.service.DataService;
import com.openclassroom.SafetyNet.Alerts.service.PersonCrudService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PersonController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                Saml2RelyingPartyAutoConfiguration.class
        }
)

public class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PersonCrudService personCrudService;

    @Test
    void getAll_ok() throws Exception {
        Person p = new Person();
        p.setFirstName("Chandler");
        p.setLastName("Bing");
        when(personCrudService.getAll()).thenReturn(List.of(p));

        mvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].firstName").value("Chandler"))
                .andExpect(jsonPath("$[0].lastName").value("Bing"));
    }

    @Test
    void post_created() throws Exception {
        when(personCrudService.create(any(Person.class))).thenReturn(true);

        mvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                   {"firstName":"Chandler","lastName":"Bing","address": "Central Perk","city":"New York","zip":"z","phone":"p","email":"e"}
                                """))
                .andExpect(status().isCreated());
    }
}
