package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordCrudService {

    private final DataService dataService;

    public List<MedicalRecord> getAll() {
        return dataService.getMedicalrecords();
    }

    public boolean create(MedicalRecord record) {
        log.info("Creating medicalRecord for {} {}", record.getFirstName(), record.getLastName());
        return dataService.addMedicalRecord(record);
    }

    public boolean update(String firstName, String lastName, MedicalRecord updates) {
        log.info("Updating medicalRecord for {} {}", firstName, lastName);
        return dataService.updateMedicalRecord(firstName, lastName, updates);
    }

    public boolean delete(String firstName, String lastName) {
        log.info("Deleting medicalRecord for {} {}", firstName, lastName);
        return dataService.deleteMedicalRecord(firstName, lastName);
    }
}
