package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.PersonInfoDTO;
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
public class PersonInfoServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    PersonInfoService personInfoService;

    @Test
    void getPersonInfoByLastName_returnsMergedDataFromMR() {

        var p1 = person("Monica","Geller","12 Monica St","monica.geller@gmail.com");
        var p2 = person("Ross","Geller","12 Ross St","ross.geller@gmail.com");
        var p3 = person("Rachel","Green","99 Rachel St","rachel.green@gmail.com");

        when(dataService.getPersons()).thenReturn(List.of(p1,p2,p3));

        var mr1 =mr("Monica","Geller","01/01/1990", List.of("aznol:350mg"), List.of());
        var mr2 =mr("Ross","Geller","01/01/1980", List.of(), List.of("peanut"));
        var mr3 =mr("Rachel","Green","09/09/1999", List.of(), List.of());

        when(dataService.getMedicalrecords()).thenReturn(List.of(mr1,mr2,mr3));

        var result = personInfoService.getPersonInfoByLastName("Geller");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PersonInfoDTO::getFirstName)
                .containsExactlyInAnyOrder("Monica","Ross");

        var monica = result.stream().filter(r -> r.getFirstName().equals("Monica")).findFirst().get();
        assertThat(monica.getAddress()).isEqualTo("12 Monica St");
        assertThat(monica.getEmail()).isEqualTo("monica.geller@gmail.com");
        assertThat(monica.getMedications()).containsExactly("aznol:350mg");
        assertThat(monica.getAllergies()).isEmpty();
        assertThat(monica.getAge()).isGreaterThan(30);
    }

    @Test
    void getPersonInfoByLastName_emptyWhenNoMatch() {
        when(dataService.getPersons()).thenReturn(List.of());
        when(dataService.getMedicalrecords()).thenReturn(List.of());

        var result = personInfoService.getPersonInfoByLastName("Unknown");
        assertThat(result).isEmpty();
    }

    private static Person person(String first, String last, String address, String email) {
        var p =new Person();
        p.setFirstName(first);
        p.setLastName(last);
        p.setAddress(address);
        p.setEmail(email);
        return p;
    }

    private static MedicalRecord mr(String first, String last, String birthdate, List<String> medications, List<String> allergies) {
        var m =new MedicalRecord();
        m.setFirstName(first);
        m.setLastName(last);
        m.setBirthdate(birthdate);
        m.setMedications(medications);
        m.setAllergies(allergies);
        return m;
    }
}
