package com.openclassroom.SafetyNet.Alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("it")
@SpringBootTest
@AutoConfigureMockMvc
public class PersonCrudIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @TempDir
    Path tempDir;
    Path tempJsonPath;

    @BeforeEach
    void setUp() throws Exception {
        // je copie data-test dans un endroit temporeen et lui envoie dataService
        tempJsonPath = tempDir.resolve("data.json");
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data-test.json")) {
            if (input == null) throw new IllegalStateException("data-test.json not found in classpath");
            Files.write(tempJsonPath, FileCopyUtils.copyToByteArray(input));
        }

        System.setProperty("safetynet.data.path", tempJsonPath.toString());
    }

    @Test
    void fullCrud_person() throws Exception {
        // POST nouvelle personne
        String newPerson = """
                {
                "firstName":"Chandler",
                "lastName":"Bing",
                "address":"Chandler St",
                "city":"New York",
                "zip":"10001",
                "phone":"111-111-1111",
                "email":"chandler.bing@gmail.com"
                }
                """;
        mvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newPerson))
                .andExpect(status().isCreated());

        // GET ALL avec Chandler Bing
        mvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].firstName", hasItem("Chandler")))
                .andExpect(jsonPath("$[*].lastName", hasItem("Bing")));

        // PUT mise à jour de Chandler Bing
        String updates = """
                {
                "address":"Central Perk St",
                "city":"New York",
                "zip":10044,
                "phone":"222-222-2222",
                "email":"miss.chanandler.bong@gmail.com"
                }
                """;

        mvc.perform(put("/person?firstName=Chandler&lastName=Bing")
                .contentType(MediaType.APPLICATION_JSON)
        .content(updates))
                .andExpect(status().isOk());

        // GET ALL avec modifications
        mvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.firstName=='Chandler' && @.lastName=='Bing')].address").value(hasItem("Central Perk St")))
                .andExpect(jsonPath("$[?(@.firstName=='Chandler'&& @.lastName=='Bing')].phone").value(hasItem("222-222-2222")));

        // DELETE Chandler Bing
        mvc.perform(delete("/person?firstName=Chandler&lastName=Bing"))
                .andExpect(status().isNoContent());

        // GET ALL final sans Chandler Bing
        mvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].firstName").value(org.hamcrest.Matchers.not(hasItem("Chandler"))));

    }

}
