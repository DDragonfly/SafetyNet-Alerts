package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.FireAddressDTO;
import com.openclassroom.SafetyNet.Alerts.dto.ResidentAtAddressDTO;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireAddressService {

    private final DataService dataService;

    public FireAddressDTO getFireInfoByAddress(String address) {
        log.info("Calculating /fire for address '{}'", address);

        // trouve les stations qui servent cette adresse
        List<String> stationNumbers = dataService.getFirestations().stream()
                .filter(fs -> address.equalsIgnoreCase(fs.getAddress()))
                .map(Firestation::getStation)
                .distinct()
                .toList();

        // si pas des stations pour l'adresse -> DTO vide
        if (stationNumbers.isEmpty()) {
            log.info("No firestation found for address '{}'", address);
            FireAddressDTO empty = new FireAddressDTO();
            empty.setStationNumber(null);
            empty.setResidents(List.of());
            return empty;
        }

        // si plusieurs mapping pour le meme adresse, prends le premier mais montre le log
        if (stationNumbers.size() > 1) {
            log.debug("Multiple stations {} mapped to address '{}'; showing the first one", stationNumbers, address);
        }
        String stationNumber = stationNumbers.get(0);

        // Toutes les residents de l'adresse
        List<Person> residents = dataService.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .toList();

        // map MedicalRecord
        Map<String, MedicalRecord> mrByKey = dataService.getMedicalrecords().stream()
                .collect(Collectors.toMap(
                        mr -> key(mr.getFirstName(), mr.getLastName()),
                        Function.identity(),
                        (a, b) -> a // Si duplicats, tiens le premier
                ));

        // construit les residentsDTO
        List<ResidentAtAddressDTO> residentDTOs = new ArrayList<>();
        for (Person p : residents) {
            MedicalRecord mr = mrByKey.get(key(p.getFirstName(), p.getLastName()));

            int age = (mr == null || mr.getBirthdate() == null)
                    ? 200 : calculateAge(mr.getBirthdate()); // on ne connait pas l'âge

            ResidentAtAddressDTO dto = new ResidentAtAddressDTO();
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setPhone(p.getPhone());
            dto.setAge(age);
            dto.setMedications(mr != null && mr.getMedications() != null ? mr.getMedications() : List.of());
            dto.setAllergies(mr != null && mr.getAllergies() != null ? mr.getAllergies() : List.of());
            residentDTOs.add(dto);
        }

        // prepare la réponse
        FireAddressDTO response = new FireAddressDTO();
        response.setStationNumber(stationNumber);
        response.setResidents(residentDTOs);

        log.info("/fire '{}': station={}, residents={}", address, stationNumber, residentDTOs.size());
        return response;
    }

    private static String key(String first, String last) {
        return first + "|" + last;
    }

    private int calculateAge(String birthdate) {
        LocalDate birth = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
