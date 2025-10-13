package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataServiceTest {

    @Autowired
    private DataService dataService;

    @Test
    public void testGetPersons_NotEmpty() {
        List<Person> persons = dataService.getPersons();

        assertNotNull(persons, "La liste ne doit pas être NULL");
        assertFalse(persons.isEmpty(), "La liste des persons ne doit pas être vide");

        System.out.println("Prémier résultat personne: " + persons.get(0).getFirstName());
    }

    @Test
    public void testGetFireStation_NotEmpty() {
        List<Firestation> firestations = dataService.getFirestations();

        assertNotNull(firestations, "La liste ne doit pas être NULL");
        assertFalse(firestations.isEmpty(), "La liste des fire station ne doit pas être vide");

        System.out.println("Prémier résultat firestation: " + firestations.get(0).getAddress());
    }

    @Test
    public void testGetMedicalRecord_NotEmpty() {
        List<MedicalRecord> medicalRecords = dataService.getMedicalrecords();

        assertNotNull(medicalRecords, "La liste ne doit pas être NULL");
        assertFalse(medicalRecords.isEmpty(), "La liste des medical records ne doit pas être vide");

        System.out.println("Prémier résultat medical record: " + medicalRecords.get(0).getFirstName());
    }


}
