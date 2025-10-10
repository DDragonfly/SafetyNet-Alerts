package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.ChildAlertDTO;
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
public class ChildAlertServiceUnitTest {

    @Mock DataService dataService;
    @InjectMocks ChildAlertService childService;

    @Test
    void children_found_withHouseholdMembers() {
        when(dataService.getPersons()).thenReturn(List.of(
        person("Giacomino", "Poretti", "12 Il Trio St"),
                person("Aldo", "Baglio", "12 Il Trio St"),
                person("Giovanni", "Storti", "12 Il Trio St"),
                person("Checco", "Zalone", "99 Checco St")
        ));

        when(dataService.getMedicalrecords()).thenReturn(List.of(
                mr("Giacomino", "Poretti", "01/01/2020"),
                mr("Aldo", "Baglio", "01/01/1990"),
                mr("Giovanni", "Storti", "01/01/1980")
        ));

        List<ChildAlertDTO> result = childService.getChildrenByAddress("12 Il Trio St");

        assertThat(result).hasSize(1);
        ChildAlertDTO giacomino = result.get(0);
        assertThat(giacomino.getFirstName()).isEqualTo("Giacomino");
        assertThat(giacomino.getLastName()).isEqualTo("Poretti");
        assertThat(giacomino.getAge()).isLessThan(18);
        assertThat(giacomino.getHouseholdMembers())
                .extracting(h -> h.getFirstName() + " " + h.getLastName())
                .containsExactlyInAnyOrder("Aldo Baglio", "Giovanni Storti");
    }

    @Test
    void noChildren_returnsEmptyList() {
        when(dataService.getPersons()).thenReturn(List.of(
                person("Aldo", "Baglio", "12 Il Trio St"),
                person("Giovanni", "Storti", "12 Il Trio St")
        ));
        when(dataService.getMedicalrecords()).thenReturn(List.of(
                mr("Aldo", "Baglio", "01/01/1990"),
                mr("Giovanni", "Storti", "01/01/1980")
        ));

        List<ChildAlertDTO> result = childService.getChildrenByAddress("12 Il Trio St");
        assertThat(result).isEmpty();
    }

    private static Person person(String name, String last, String address) {
        var p = new Person();
        p.setFirstName(name);
        p.setLastName(last);
        p.setAddress(address);
        return p;
    }

    private static MedicalRecord mr(String name, String last, String birthday) {
        var mr = new MedicalRecord();
        mr.setFirstName(name);
        mr.setLastName(last);
        mr.setBirthdate(birthday);
        return mr;
    }
}
