package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.service.MedicalRecordCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/medicalRecord")
public class MedicalRecordCrudController {

    private final MedicalRecordCrudService medicalRecordCrudService;

    // GET
    @GetMapping("/_all")
    public List<MedicalRecord> all() {
        return medicalRecordCrudService.getAll();
    }

    // POST /medicalRecord -> creation nouveau dossier
    @PostMapping
    public ResponseEntity<?> create(@RequestBody MedicalRecord body) {
        if (isBlank(body.getFirstName()) || isBlank(body.getLastName()) || isBlank(body.getBirthdate())) {
            return ResponseEntity.badRequest().body("First Name, Last Name and birthdate are required");
        }

        if (body.getMedications() == null) body.setMedications(List.of());
        if (body.getAllergies() == null) body.setAllergies(List.of());

        boolean created = medicalRecordCrudService.create(body);
        if (!created) {
            return ResponseEntity.status(409).body("Medical record already exists");
        }
        return ResponseEntity.status(201).build();
    }

    // PUT /medicalRecord?firstName & lastName -> mise à jour date naissance, med, allergies
    @PutMapping
    public ResponseEntity<?> update(@RequestParam String firstName, @RequestParam String lastName, @RequestBody MedicalRecord updates) {
        boolean ok = medicalRecordCrudService.update(firstName, lastName, updates);
        if (!ok) {
            return ResponseEntity.status(404).body("Medical record not found");
        }
        return ResponseEntity.ok().build();
    }

    // DELETE /medicalRecord?firstName lastName
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String firstName, @RequestParam String lastName) {
        boolean ok = medicalRecordCrudService.delete(firstName, lastName);
        if (!ok) {
            return ResponseEntity.status(404).body("Medical record not found");
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
