package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordCrudServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    MedicalRecordCrudService medicalRecordCrudService;

    @Test
    void create_update_delete_delegate() {
        var mr = new MedicalRecord();
        mr.setFirstName("Phoebe");
        mr.setLastName("Buffay");

        when(dataService.addMedicalRecord(mr)).thenReturn(true);
        assertThat(medicalRecordCrudService.create(mr)).isTrue();
        verify(dataService).addMedicalRecord(mr);

        when(dataService.updateMedicalRecord("Phoebe","Buffay",mr)).thenReturn(false);
        assertThat(medicalRecordCrudService.update("Phoebe","Buffay",mr)).isFalse();
        verify(dataService).updateMedicalRecord("Phoebe","Buffay",mr);

        when(dataService.deleteMedicalRecord("Phoebe","Buffay")).thenReturn(true);
        assertThat(medicalRecordCrudService.delete("Phoebe","Buffay")).isTrue();
        verify(dataService).deleteMedicalRecord("Phoebe","Buffay");
    }
}
