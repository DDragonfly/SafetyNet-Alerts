package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
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
public class PhoneAlertServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    PhoneAlertService phoneservice;

    @Test
    void getPhonesByStation_returnUniqueSorted_IgnoreNulls() {
        // stationne 1 qui couvre A et B, stationne 2 couvres C
        when(dataService.getFirestations()).thenReturn(List.of(
                firestation("A St", "1"),
                firestation("B St", "1"),
                firestation("C St", "2")
        ));

        // personne: numerós dupliqués, null et adress non couvert
        when(dataService.getPersons()).thenReturn(List.of(
                person("A St", "111-111-1111"),
                person("A St", "222-222-2222"),
                person("B St", "222-222-2222"), //personne numeró dupliquée
                person("B St", null), //à ignorer
                person("C St", "999-999-9999")
        ));

        var phones = phoneservice.getPhoneByStation("1");

        assertThat(phones).containsExactly("111-111-1111", "222-222-2222");

    }

    @Test
    void getPhonesByStation_emptyWhenStationHasNoAddress() {
        when(dataService.getFirestations()).thenReturn(List.of(
                firestation("X", "3"),
                firestation("Y", "4")
        ));

        var phones = phoneservice.getPhoneByStation("9");
        assertThat(phones).isEmpty();
    }

    private static Firestation firestation(String address, String station) {
        var fs = new Firestation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }

    private static Person person(String address, String phone) {
        var p = new Person();
        p.setAddress(address);
        p.setPhone(phone);
        return p;
    }
}
