package com.openclassroom.SafetyNet.Alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassroom.SafetyNet.Alerts.model.DataWrapper;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DataService {

    private DataWrapper dataWrapper;

    private final ObjectMapper mapper = new ObjectMapper();

        @PostConstruct
        public void init () {
            log.info("Loading data.json from classpath...");
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.json")) {
                if (inputStream == null) {
                    log.error("data.json not found on classpath: /resources/data.json");
                    throw new IllegalStateException("data.json not found");
                }

                this.dataWrapper = mapper.readValue(inputStream, DataWrapper.class);
                log.info("Data loaded: {} persons, {} firestations, {} medical records",
                        dataWrapper.getPersons().size(),
                        dataWrapper.getFirestations().size(),
                        dataWrapper.getMedicalrecords().size());
            } catch (Exception e) {
                log.error("Failed to load data.json: {}", e.getMessage(), e);
                throw new IllegalStateException("Cannot bootstrap application data", e);
            }
        }

    private void saveToFile() {
        try {
            File file = new File("src/main/resources/data.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, dataWrapper);
        } catch (IOException e) {
            log.error("Failed to write data.json: {}", e.getMessage(), e);
        }
    }

    public void persist() {
            saveToFile();
    }

        // GETTERS

        public List<Person> getPersons () {
            return dataWrapper != null ? dataWrapper.getPersons() : List.of();
        }

        public List<Firestation> getFirestations () {
            return dataWrapper != null ? dataWrapper.getFirestations() : List.of();
        }

        public List<MedicalRecord> getMedicalrecords () {
            return dataWrapper != null ? dataWrapper.getMedicalrecords() : List.of();
        }

        // PERSON : CRUD helpers

        public Optional<Person> findPerson (String first, String last){
            if (dataWrapper == null) return Optional.empty();
            return dataWrapper.getPersons().stream()
                    .filter(p -> p.getFirstName().equalsIgnoreCase(first)
                            && p.getLastName().equalsIgnoreCase(last))
                    .findFirst();
        }

        // POST Ajout nouvelle personne si pas existante
        public boolean addPerson (Person p){
            if (dataWrapper == null) return false;
            boolean exists = findPerson(p.getFirstName(), p.getLastName()).isPresent();
            if (exists) return false;
            boolean added = dataWrapper.getPersons().add(p);
            if (added) persist();
            return added;
        }

        // PUT Mise à jour personne existante
        public boolean updatePerson (String first, String last, Person updates){
            if (dataWrapper == null) return false;
            var opt = findPerson(first, last);
            if (opt.isEmpty()) return false;

            Person target = opt.get();
            target.setAddress(updates.getAddress());
            target.setCity(updates.getCity());
            target.setZip(updates.getZip());
            target.setPhone(updates.getPhone());
            target.setEmail(updates.getEmail());
            persist();
            return true;
        }

        // DELETE Supprimer une personne
        public boolean deletePerson (String first, String last){
            if (dataWrapper == null) return false;
            boolean removed = dataWrapper.getPersons().removeIf(p -> p.getFirstName().equalsIgnoreCase(first) && p.getLastName().equalsIgnoreCase(last));
            if (removed) persist();
            return removed;
        }

        // FIRESTATION: CRUD helpers

        public Optional<Firestation> findFirestationByAddress (String address){
            if (dataWrapper == null) return Optional.empty();
            return dataWrapper.getFirestations().stream().filter(fs -> fs.getAddress().equalsIgnoreCase(address)).findFirst();
        }

        // POST ajout d'un mapping caserme/addresse
        public boolean addFirestation (Firestation mapping){
            if (dataWrapper == null) return false;
            boolean exists = findFirestationByAddress(mapping.getAddress()).isPresent();
            if (exists) return false;
            boolean added = dataWrapper.getFirestations().add(mapping);
            if (added) persist();
            return added;
        }

        // PUT Mise à jour du numéro de la caserne de pompiers d'une adresse
        public boolean updateFirestationStation (String address, String newStation){
            if (dataWrapper == null) return false;
            String target = address == null ? "" : address.trim();
            String station = newStation == null ? "" : newStation.trim();

            boolean ok = dataWrapper.getFirestations().stream()
                    .filter(fs -> fs.getAddress() != null && fs.getAddress().trim().equalsIgnoreCase(target))
                    .findFirst()
                    .map(fs -> {
                        fs.setStation(station);
                        return true;
                    })
                    .orElse(false);
            if (ok) persist();
            return ok;
        }

        // DELETE supprime le mapping d'une adresse
        public boolean deleteFirestationByAddress (String address){
            if (dataWrapper == null) return false;
            String target = address == null ? "" : address.trim();
            boolean removed = dataWrapper.getFirestations().removeIf(fs -> fs.getAddress() != null && fs.getAddress().trim().equalsIgnoreCase(target));
            if (removed) persist();
            return removed;
        }

        // DELETE supprime le mapping d'une caserme
        public boolean deleteFirestationsByStation (String station){
            if (dataWrapper == null) return false;
            String target = station == null ? "" : station.trim();
            var list = dataWrapper.getFirestations();
            int before = list.size();
            list.removeIf(fs -> fs.getStation() != null && fs.getStation().trim().equalsIgnoreCase(target));
            boolean changed = before != list.size();
            if (changed) persist();
            return changed;
        }

        // MEDICAL RECORD: CRUD helpers

        public Optional<MedicalRecord> findMedicalRecord (String first, String last){
            if (dataWrapper == null) return Optional.empty();
            return dataWrapper.getMedicalrecords().stream()
                    .filter(mr -> mr.getFirstName().equalsIgnoreCase(first)
                            && mr.getLastName().equalsIgnoreCase(last))
                    .findFirst();
        }

        // POST: ajoute d'un dossier medical

        public boolean addMedicalRecord (MedicalRecord record){
            if (dataWrapper == null) return false;
            boolean exists = findMedicalRecord(record.getFirstName(), record.getLastName()).isPresent();
            if (exists) return false;
            boolean added = dataWrapper.getMedicalrecords().add(record);
            if (added) persist();
            return added;
        }

        // PUT: mise à jour d'un dossier
        public boolean updateMedicalRecord (String first, String last, MedicalRecord updates){
            if (dataWrapper == null) return false;
            var opt = findMedicalRecord(first, last);
            if (opt.isEmpty()) return false;

            MedicalRecord target = opt.get();
            target.setBirthdate(updates.getBirthdate());
            target.setMedications(updates.getMedications() != null ? updates.getMedications() : List.of());
            target.setAllergies(updates.getAllergies() != null ? updates.getAllergies() : List.of());
            persist();
            return true;
        }

        // DELETE: supprime un dossier medical

        public boolean deleteMedicalRecord (String first, String last){
            if (dataWrapper == null) return false;
            boolean removed = dataWrapper.getMedicalrecords().removeIf(mr -> mr.getFirstName().equalsIgnoreCase(first) && mr.getLastName().equalsIgnoreCase(last));
            if (removed) persist();
            return removed;
        }
    }
