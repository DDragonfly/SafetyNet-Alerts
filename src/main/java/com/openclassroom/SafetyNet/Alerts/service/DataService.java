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

    // POST Ajout nouvelle personne si pas existante
    public boolean addPerson(Person p) {
        if (dataWrapper == null) return false;
        boolean exists = findPerson(p.getFirstName(), p.getLastName()).isPresent();
        if (exists) return false;
        return dataWrapper.getPersons().add(p);
    }

    // PUT Mise à jour personne existante
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

    // DELETE Supprimer une personne
    public boolean deletePerson(String first, String last) {
        if (dataWrapper == null) return false;
        return dataWrapper.getPersons().removeIf(p -> p.getFirstName().equalsIgnoreCase(first) && p.getLastName().equalsIgnoreCase(last));
    }

    // FIRESTATION: CRUD helpers

    public Optional<Firestation> findFirestationByAddress(String address) {
        if (dataWrapper == null) return Optional.empty();
        return dataWrapper.getFirestations().stream().filter(fs -> fs.getAddress().equalsIgnoreCase(address)).findFirst();
    }

    // POST ajout d'un mapping caserme/addresse
    public boolean addFirestation(Firestation mapping) {
        if (dataWrapper == null) return false;
        boolean exists = findFirestationByAddress(mapping.getAddress()).isPresent();
        if (exists) return false;
        return dataWrapper.getFirestations().add(mapping);
    }

    // PUT Mise à jour du numéro de la caserne de pompiers d'une adresse
    public boolean updateFirestationStation(String address, String newStation) {
        if (dataWrapper == null) return false;
        String target = address == null ? "" : address.trim();
        String station = newStation == null ? "" : newStation.trim();

        return dataWrapper.getFirestations().stream()
                .filter(fs -> fs.getAddress() != null && fs.getAddress().trim().equalsIgnoreCase(target))
                .findFirst()
                .map(fs -> { fs.setStation(station); return true; })
                .orElse(false);
    }

    // DELETE supprime le mapping d'une adresse
    public boolean deleteFirestationByAddress(String address) {
        if (dataWrapper == null) return false;
        String target = address == null ? "" : address.trim();
        return dataWrapper.getFirestations().removeIf(fs -> fs.getAddress() != null && fs.getAddress().trim().equalsIgnoreCase(target));
    }

    // DELETE supprime le mapping d'une caserme
    public boolean deleteFirestationsByStation(String station) {
        if (dataWrapper == null) return false;
        String target = station == null ? "" : station.trim();
        var list = dataWrapper.getFirestations();
        int before = list.size();
        list.removeIf(fs -> fs.getStation() != null && fs.getStation().trim().equalsIgnoreCase(target));
        return before != list.size();
    }

}
