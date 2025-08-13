package com.openclassroom.SafetyNet.Alerts.service;


import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneAlertService {
    private final DataService dataService;

    public List<String> getPhoneByStation(String stationNumber) {
        log.info("Calculating phoneAlert for station {}", stationNumber);

        // addresses uniques couverts par la station
        Set<String> addresses = dataService.getFirestations().stream()
                .filter(f -> f.getStation().equals(stationNumber))
                .map(Firestation::getAddress)
                .collect(Collectors.toSet());

        if (addresses.isEmpty()) {
            log.info("No addresses mapped to station {}", stationNumber);
            return List.of();
        }

        // numeros de telephones uniques des residents a ces addresses
        // avec TreeSet pour des resultats ordonnes et sans duplicats
        Set<String> phones = dataService.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));

        log.info("phoneAlert for station {}: phones={}",  stationNumber, phones.size());
        return new ArrayList<>(phones);
    }
}
