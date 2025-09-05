package com.openclassroom.SafetyNet.Alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.SafetyNet.Alerts.model.DataWrapper;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DataService {

    private DataWrapper dataWrapper;

    @PostConstruct
    public void init(){
        log.info("Loading data.json from classpath...");
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json")) {
            if (inputStream == null) {
                log.error("data.json not found on classpath: /resources/data.json");
                throw new IllegalStateException("data.json not found");
            }
            ObjectMapper mapper = new ObjectMapper();

            DataWrapper raw = mapper.readValue(inputStream, DataWrapper.class);

            this.dataWrapper = raw;
            log.info("Data loaded: {} persons, {} firestations, {} medical records",
                    dataWrapper.getPersons().size(),
                    dataWrapper.getFirestations().size(),
                    dataWrapper.getMedicalrecords().size());
        } catch (Exception e) {
            log.error("Failed to load data.json: {}", e.getMessage(), e);
            throw new IllegalStateException("Cannot bootstrap application data", e);
        }
    }

    // GETTERS

    public List<Person> getPersons() {
        return dataWrapper != null ? dataWrapper.getPersons() : List.of();
    }

    public List<Firestation> getFirestations() {
        return dataWrapper != null ? dataWrapper.getFirestations() : List.of();
    }

    public List<MedicalRecord> getMedicalrecords() {
        return dataWrapper != null ? dataWrapper.getMedicalrecords() : List.of();
    }

    // PERSON : CRUD helpers

    public Optional<Person> findPerson(String first, String last) {
        if (dataWrapper == null) return Optional.empty();
        return dataWrapper.getPersons().stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(first)
                        && p.getLastName().equalsIgnoreCase(last))
                .findFirst();
    }

    // Ajout nouvelle personne si pas existante
    public boolean addPerson(Person p) {
        if (dataWrapper == null) return false;
        boolean exists = findPerson(p.getFirstName(), p.getLastName()).isPresent();
        if (exists) return false;
        return dataWrapper.getPersons().add(p);
    }

    // Mise à jour personne existante
    public boolean updatePerson(String first, String last, Person updates) {
        if (dataWrapper == null) return false;
        var opt = findPerson(first, last);
        if (opt.isEmpty()) return false;

        Person target = opt.get();
        target.setAddress(updates.getAddress());
        target.setCity(updates.getCity());
        target.setZip(updates.getZip());
        target.setPhone(updates.getPhone());
        target.setEmail(updates.getEmail());
        return true;
    }

    // Supprimer une personne
    public boolean deletePerson(String first, String last) {
        if (dataWrapper == null) return false;
        return dataWrapper.getPersons().removeIf(p -> p.getFirstName().equalsIgnoreCase(first) && p.getLastName().equalsIgnoreCase(last));
    }
}
