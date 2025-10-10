package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.AddressHouseholdDTO;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FloodStationsServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    FloodStationsService floodStationsService;

    @Test
    void getHouseholdsByStations_collectsByAddress() {
        // stationne 1 couvre A et B, stationne 2 couvre C
        when(dataService.getFirestations()).thenReturn(List.of(fs("A", "1"), fs("B", "1"), fs("C", "2"), fs("Z", "9")));

        when(dataService.getPersons()).thenReturn(List.of(
                person("Phoebe", "Buffay", "A"),
                person("Mike", "Hannigan", "A"),
                person("Gunther", "Centralperk", "B"),
                person("Janice", "Litman", "C"),
                person("NotFan", "OfFriends", "Z")
        ));

        when(dataService.getMedicalrecords()).thenReturn(List.of(
                mr("Phoebe", "Buffay", "01/01/1990", List.of("vitamin:500"), List.of()),
                mr("Mike", "Hannigan", "03/03/1983", List.of(), List.of()),
                mr("Gunther", "CentralPerk", "01/01/1990", List.of("vitamin:500"), List.of()),
                mr("Janice", "Litman", "04/04/2020", List.of("med:5"), List.of("cats"))
        ));

        var result = floodStationsService.getHouseholdsByStations(List.of("1","2"));

        var map = result.stream()
                .collect(java.util.stream.Collectors.toMap( AddressHouseholdDTO::getAddress,AddressHouseholdDTO::getResidents));

        assertThat(map.keySet()).containsExactlyInAnyOrder("A", "B", "C");

        assertThat(map.get("A"))
                .extracting(r -> r.getFirstName() + " " + r.getLastName())
                .containsExactlyInAnyOrder("Phoebe Buffay", "Mike Hannigan");

        assertThat(map.get("B"))
                .extracting(r -> r.getFirstName() + " " + r.getLastName())
                .containsExactlyInAnyOrder("Gunther Centralperk");

        assertThat(map.get("C"))
                .extracting(r -> r.getFirstName() + " " + r.getLastName())
                .containsExactlyInAnyOrder("Janice Litman");

        var janice = map.get("C").get(0);
        assertThat(janice.getAge()).isLessThan(18);
        assertThat(janice.getAllergies()).contains("cats");
    }

    @Test
    void getHouseholdsByStations_emptyWhenNoStations() {
        var result = floodStationsService.getHouseholdsByStations(List.of());
        assertThat(result).isEmpty();
    }

    private static Firestation fs(String address, String station) {
        var f = new Firestation();
        f.setAddress(address);
        f.setStation(station);
        return f;
    }

    private static Person person (String first, String last, String address) {
        var p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(address);
        return p;
    }

    private static MedicalRecord mr (String first, String last, String birthdate, List<String> medications, List<String> allergies) {
        var m = new MedicalRecord();
        m.setFirstName(first);
        m.setLastName(last);
        m.setBirthdate(birthdate);
        m.setMedications(medications);
        m.setAllergies(allergies);
        return m;
    }
}
