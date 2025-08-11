package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.FirestationCoverageDTO;
import com.openclassroom.SafetyNet.Alerts.dto.PersonCoveredDTO;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FirestationService {

    @Autowired
    private DataService dataService;

    public FirestationCoverageDTO getCoverageByStationNumber(String stationNumber){

        log.info("Calculating coverage for station {}", stationNumber);

        List<Firestation> firestations = dataService.getFirestations();
        List<String> addresses = firestations.stream()
                .filter(f -> f.getStation().equals(stationNumber))
                .map(Firestation::getAddress)
                .toList();

        if (addresses.isEmpty()){
            FirestationCoverageDTO empty = new FirestationCoverageDTO();
            empty.setPersons((List.of()));
            empty.setAdultCount(0);
            empty.setChildCount(0);
            return empty;
        }

        List<Person> persons = dataService.getPersons();
        List<MedicalRecord> records = dataService.getMedicalrecords();

        List<PersonCoveredDTO> coveredPersons = new ArrayList<>();
        int adultCount = 0;
        int childCount = 0;

        for (Person person : persons) {
            if (addresses.contains(person.getAddress())) {
                //trouve l'âge
                Optional<MedicalRecord> record = records.stream()
                        .filter(mr -> mr.getFirstName().equals(person.getFirstName())
                                && mr.getLastName().equals(person.getLastName()))
                        .findFirst();

                int age = record.map(this::calculateAge).orElse(0);
                if (age >= 18) {
                    adultCount++;
                } else {
                    childCount++;
                }

                // creer le DTO à ajouter
                PersonCoveredDTO dto = new PersonCoveredDTO();
                dto.setFirstName(person.getFirstName());
                dto.setLastName(person.getLastName());
                dto.setAddress(person.getAddress());
                dto.setPhone(person.getPhone());
                coveredPersons.add(dto);

            }
        }

        FirestationCoverageDTO result = new FirestationCoverageDTO();
        result.setPersons(coveredPersons);
        result.setAdultCount(adultCount);
        result.setChildCount(childCount);
        log.info("Coverage: persons={}, adults={}, children={}", coveredPersons.size(), adultCount, childCount);
        return result;
    }

    private int calculateAge(MedicalRecord record) {
        LocalDate birthDate = LocalDate.parse(record.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
