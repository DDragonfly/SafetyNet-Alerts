package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FirestationCrudServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    FirestationCrudService firestationCrudService;

    @Test
    void create_delegatesToDataService() {
        var fs = mapping("A","1");
        when(dataService.addFirestation(fs)).thenReturn(true);

        assertThat(firestationCrudService.create(fs)).isTrue();
        verify(dataService).addFirestation(fs);
    }

    @Test
    void updateStationForAddress_callsDataService() {
        when(dataService.updateFirestationStation("A","9")).thenReturn(true);
        assertThat(firestationCrudService.updateStationForAddress("A","9")).isTrue();
        verify(dataService).updateFirestationStation("A","9");
    }

    @Test
    void deleteByAddress_and_deleteByStation_callsDataService() {
        when(dataService.deleteFirestationByAddress("A")).thenReturn(true);
        when(dataService.deleteFirestationsByStation("1")).thenReturn(false);

        assertThat(firestationCrudService.deleteByAddress("A")).isTrue();
        assertThat(firestationCrudService.deleteByStation("1")).isFalse();

        verify(dataService).deleteFirestationByAddress("A");
        verify(dataService).deleteFirestationsByStation("1");
    }


    private static Firestation mapping(String address, String station) {
        var fs = new Firestation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }



}
