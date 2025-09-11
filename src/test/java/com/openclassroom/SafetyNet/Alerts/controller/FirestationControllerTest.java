package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.FirestationCoverageDTO;
import com.openclassroom.SafetyNet.Alerts.service.FirestationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirestationController.class)
public class FirestationControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    FirestationService firestationService;

    @Test
    void getCoverage_ok() throws Exception {
        var dto = new FirestationCoverageDTO();
        dto.setAdultCount(5); dto.setChildCount(1);
        dto.setPersons(java.util.List.of());
        when(firestationService.getCoverageByStationNumber("1")).thenReturn(dto);

        mvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultCount").value(5))
                .andExpect(jsonPath("$.childCount").value(1))
                .andExpect(jsonPath("$.persons").isArray());
    }
}
