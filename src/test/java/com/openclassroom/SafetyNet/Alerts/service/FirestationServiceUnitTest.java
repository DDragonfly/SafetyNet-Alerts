package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.FirestationCoverageDTO;
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
public class FirestationServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    FirestationService fsService;

    @Test
    void coverage_countsPeople_FiltersByAddresses() {
        // firestations : la statione 1 couvre A et B

        var fsA = new Firestation();
        fsA.setAddress("A");
        fsA.setStation("1");
        var fsB = new Firestation();
        fsB.setAddress("B");
        fsB.setStation("1");
        var fsC = new Firestation();
        fsC.setAddress("C");
        fsC.setStation("2");
        when(dataService.getFirestations()).thenReturn(List.of(fsA, fsB, fsC));

        // persons : 3 personnes sur A et B, 1 personne sur C
        var p1 = person("Aldo", "Baglio", "A", "111");
        var p2 = person("Giovanni", "Storti", "B", "222");
        var p3 = person("Giacomino", "Poretti", "B", "333");
        var p4 = person("Checco", "Zalone", "C", "999");
        when(dataService.getPersons()).thenReturn(List.of(p1, p2, p3, p4));

        // medical records pour calcul de l'age
        var m1 = mr("Aldo", "Baglio", "01/01/1980");
        var m2 = mr("Giovanni", "Storti", "02/02/1980");
        var m3 = mr("Giacomino", "Poretti", "03/03/2020");
        var m4 = mr("Checco", "Zalone", "12/12/1950");
        when(dataService.getMedicalrecords()).thenReturn(List.of(m1, m2, m3, m4));

        FirestationCoverageDTO dto = fsService.getCoverageByStationNumber("1");

        assertThat(dto.getPersons()).hasSize(3);
        assertThat(dto.getAdultCount()).isEqualTo(2);
        assertThat(dto.getChildCount()).isEqualTo(1);
        assertThat(dto.getPersons())
                .extracting(p -> p.getFirstName() + " " + p.getLastName())
                .containsExactlyInAnyOrder("Aldo Baglio", "Giovanni Storti", "Giacomino Poretti");
    }

    @Test
    void coverage_emptyWhenNoAddressesForStation() {
        when(dataService.getFirestations()).thenReturn(List.of(firestation("X","1"), firestation("Y","2")));

        var dto = fsService.getCoverageByStationNumber("9");

        assertThat(dto.getPersons()).isEmpty();
        assertThat(dto.getAdultCount()).isZero();
        assertThat(dto.getChildCount()).isZero();
    }

    private static Person person(String name, String last, String address, String phone) {
        var p = new Person();
        p.setFirstName(name);
        p.setLastName(last);
        p.setAddress(address);
        p.setPhone(phone);
        return p;
    }

    private static MedicalRecord mr(String first, String last, String birthdate) {
        var m  = new MedicalRecord();
        m.setFirstName(first);
        m.setLastName(last);
        m.setBirthdate(birthdate);
        return m;
    }


    private static Firestation firestation(String address, String station) {
        var fs = new Firestation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }
}
