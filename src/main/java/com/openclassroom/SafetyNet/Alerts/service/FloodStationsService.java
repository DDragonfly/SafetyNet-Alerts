package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.AddressHouseholdDTO;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloodStationsService {

    private final DataService dataService;

    public List<AddressHouseholdDTO> getHouseholdsByStations(List<String> stationNumbers) {
        log.info("Calculating /flood/stations for {}", stationNumbers);

        if (stationNumbers == null || stationNumbers.isEmpty()) {
            return List.of();
        }

        // adresses couverts pas n'importe quelle station
        Set<String> stationSet = stationNumbers.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<String> coveredAddresses = dataService.getFirestations().stream()
                .filter(fs -> stationSet.contains(fs.getStation()))
                .map(Firestation::getAddress)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (coveredAddresses.isEmpty()) {
            log.info("No addresses for stations ()", stationNumbers);
            return List.of();
        }

        // medical records pour persons
        Map<String, MedicalRecord> mrByKey = dataService.getMedicalrecords().stream()
                .collect(Collectors.toMap(
                        mr -> key(mr.getFirstName(), mr.getLastName()),
                        Function.identity(),
                        (a, b) -> a
                ));

        // filtre pour les personnes qui habitent aux adresses couvertes
        List<Person> residents = dataService.getPersons().stream()
                .filter(p -> coveredAddresses.contains(p.getAddress()))
                .toList();

        // Groupe par addresse, avec dedup par personne (first|last)
        Map<String, LinkedHashMap<String, ResidentAtAddressDTO>> byAddress = new LinkedHashMap<>();

        for (Person p : residents) {
            String address = p.getAddress();
            String personKey = key(p.getFirstName(), p.getLastName());
            byAddress.putIfAbsent(address, new LinkedHashMap<>());

            // cree le DTO du resident
            MedicalRecord mr = mrByKey.get(personKey);
            int age = (mr == null || mr.getBirthdate() == null) ? 200 : calculateAge(mr.getBirthdate());
            ResidentAtAddressDTO dto = new ResidentAtAddressDTO();
            dto.setFirstName(p.getFirstName());
            dto.setLastName(p.getLastName());
            dto.setPhone(p.getPhone());
            dto.setAge(age);
            dto.setMedications(mr != null && mr.getMedications() != null ? mr.getMedications() : List.of());
            dto.setAllergies(mr != null && mr.getAllergies() != null ? mr.getAllergies() : List.of());

            //dedup
            byAddress.get(address).putIfAbsent(personKey, dto);
        }

        // converts la map en liste de DTO et ordonne par adresse
        List<AddressHouseholdDTO> result = byAddress.entrySet().stream()
                .map(e -> {
                    AddressHouseholdDTO dto = new AddressHouseholdDTO();
                    dto.setAddress(e.getKey());
                    dto.setResidents(new ArrayList<>(e.getValue().values()));
                    return dto;
                })
                .toList();

        log.info("/flood/stations: addresses={}, people={}",
                result.size(),
                result.stream().mapToInt(h -> h.getResidents().size()).sum());
        return result;
    }

    private static String key(String first, String last) {
        return first + "|" + last;
    }

    private int calculateAge(String birthdate) {
        LocalDate birth = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
