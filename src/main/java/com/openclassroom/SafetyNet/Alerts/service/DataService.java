package com.openclassroom.SafetyNet.Alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.SafetyNet.Alerts.model.DataWrapper;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class DataService {

    private DataWrapper dataWrapper;

    @PostConstruct
    public void init(){
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json");
            ObjectMapper mapper = new ObjectMapper();
            this.dataWrapper = mapper.readValue(inputStream, DataWrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Person> getPersons() {
        return dataWrapper != null ? dataWrapper.getPersons() : List.of();
    }

    public List<Firestation> getFirestations() {
        return dataWrapper != null ? dataWrapper.getFirestations() : List.of();
    }

    public List<MedicalRecord> getMedicalrecords() {
        return dataWrapper != null ? dataWrapper.getMedicalrecords() : List.of();
    }
}
