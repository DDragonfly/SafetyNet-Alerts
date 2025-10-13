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

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PersonInfoControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void get_byLastName_returnsListAndFields() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "Boyd"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").isNumber())
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$[0].address").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].age").exists())
                .andExpect(jsonPath("$[0].medications").isArray())
                .andExpect(jsonPath("[0].allergies").isArray());
    }

    @Test
    void get_unknownLastName_returnsEmptyArray() throws Exception {
        mvc.perform(get("/personInfo").param("lastName", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
