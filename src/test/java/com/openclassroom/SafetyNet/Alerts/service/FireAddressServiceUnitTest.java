package com.openclassroom.SafetyNet.Alerts.service;

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
public class FireAddressServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    FireAddressService fireAddressService;

    @Test
    void getFireInfoByAddress_ok() {
        when(dataService.getFirestations()).thenReturn(List.of(firestation("12 Chandler St","1"),firestation("Other","2")
        ));

        var p1 = person("Chandler","Bing","12 Chandler St","111-111-1111");
        var p2 = person("Joey","Tribbiani","12 Chandler St","222-222-2222");
        var p3 = person("Rachel","Green","Other","999-999-9999");

        when(dataService.getPersons()).thenReturn(List.of(p1,p2,p3));

        var mr1 = mr("Chandler","Bing","01/01/1990", List.of("med:10"), List.of());
        var mr2 = mr("Joey","Tribbiani","01/01/2020", List.of(), List.of("cats"));

        when(dataService.getMedicalrecords()).thenReturn(List.of(mr1, mr2));

        var dto =fireAddressService.getFireInfoByAddress("12 Chandler St");

        assertThat(dto.getStationNumber()).isEqualTo("1");
        assertThat(dto.getResidents()).hasSize(2);
        assertThat(dto.getResidents())
                .extracting(r -> r.getFirstName()+" "+r.getLastName())
                .containsExactlyInAnyOrder("Chandler Bing", "Joey Tribbiani");

        var joey =dto.getResidents().stream().filter(r -> r.getFirstName().equals("Joey")).findFirst().get();
        assertThat(joey.getAge()).isLessThan(18);
        assertThat(joey.getAllergies()).containsExactly("cats");
    }

    @Test
    void getFireInfoByAddress_unknown_returnsEmptyDTO() {
        when(dataService.getFirestations()).thenReturn(List.of(firestation("Known","3")));

        var dto = fireAddressService.getFireInfoByAddress("Unknown");
        assertThat(dto.getStationNumber()).isNull();
        assertThat(dto.getResidents()).isEmpty();
    }

    private static Firestation firestation(String address, String station) {
        var fs = new Firestation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }
    private static Person person(String first, String last, String address, String phone) {
        var p = new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(address);
        p.setPhone(phone);
        return p;
    }
    private static MedicalRecord mr(String first, String last, String birthdate, List<String> medications, List<String> allergies) {
        var mr = new MedicalRecord();
        mr.setFirstName(first);
        mr.setLastName(last);
        mr.setBirthdate(birthdate);
        mr.setMedications(medications);
        mr.setAllergies(allergies);
        return mr;
    }
}
