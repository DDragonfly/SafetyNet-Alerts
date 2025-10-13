package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.DataWrapper;
import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class DataServiceUnitTest {

    /**
     * Tests unitaires du service {@link com.openclassroom.SafetyNet.Alerts.service.DataService}.
     * <p>
     * Vérifie le chargement, la persistance et la manipulation des données en mémoire
     * à partir de fichiers JSON temporaires.
     */
    @TempDir
    public Path tmp;

    private static DataWrapper emptyDW() {
        var dw = new DataWrapper();
        dw.setPersons(new ArrayList<>());
        dw.setFirestations(new ArrayList<>());
        dw.setMedicalrecords(new ArrayList<>());
        return dw;
    }

    private static String miniJson() {
        // JSON valid pour DataWrapper
        return """
                {
                "persons": [{"firstName":"A","lastName":"B","address":"Addr","city":"X","zip":"Z","phone":"P","email":"E"}],
                "firestations":[{"address":"Addr","station":"1"}],
                "medicalrecords":[{"firstName":"A","lastName":"B","birthdate":"01/01/2000","medications":[],"allergies":[]}]
                }
                """;
    }

    @Test
    void init_fromFilesystem_successfulLoad() throws Exception {
        // temp data.json complet
        Path data = tmp.resolve("data.json");
        Files.writeString(data, miniJson());

        var svc = new DataService();
        ReflectionTestUtils.setField(svc, "dataPath", data.toString());
        ReflectionTestUtils.setField(svc, "readOnly", true);

        svc.init();

        assertThat(svc.getPersons()).hasSize(1);
        assertThat(svc.getFirestations()).hasSize(1);
        assertThat(svc.getMedicalrecords()).hasSize(1);
    }

    @Test
    void init_fileNotFound_throwsIllegalStateException() {
        var svc = new DataService();
        ReflectionTestUtils.setField(svc, "dataPath", tmp.resolve("missing.json").toString());
        ReflectionTestUtils.setField(svc, "readOnly", true);

        assertThatThrownBy(svc::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot bootstrap application data");
    }

    @Test
    void persist_readOnly_true_skipWriting() throws IOException {
        var svc = new DataService();
        Path target = tmp.resolve("out.json");

        ReflectionTestUtils.setField(svc, "dataPath", target.toString());
        ReflectionTestUtils.setField(svc, "readOnly", true);
        ReflectionTestUtils.setField(svc, "dataWrapper", emptyDW());

        // le fichier n'existe pas
        assertThat(Files.exists(target)).isFalse();

        svc.persist();

        // le fichier n'existe plus, et il n'ecrit pas
        assertThat(Files.exists(target)).isFalse();
    }

    @Test
    void persist_notReadOnly_writesFile() throws IOException {
        var svc = new DataService();
        Path target = tmp.resolve("out.json");

        ReflectionTestUtils.setField(svc, "dataPath", target.toString());
        ReflectionTestUtils.setField(svc, "readOnly", false);

        var dw = emptyDW();
        dw.getPersons().add(new Person());
        ReflectionTestUtils.setField(svc, "dataWrapper", dw);

        svc.persist();

        assertThat(target).exists();
        assertThat(Files.size(target)).isGreaterThan(2L);
    }

    @Test
    void persist_withClassPath_skipsWriteButDoesNotFail() {
        var svc = new DataService();

        ReflectionTestUtils.setField(svc, "dataPath", ("classpath:data.json"));
        ReflectionTestUtils.setField(svc, "readOnly", false);
        ReflectionTestUtils.setField(svc, "dataWrapper", emptyDW());

        assertThatCode(svc::persist).doesNotThrowAnyException();
    }

    // -------- CRUD: PERSON --------

    @Test
    void person_add_update_delete_allTrue_whenPresent() {
        var svc = new DataService();
        ReflectionTestUtils.setField(svc, "dataPath", "classpath:data.json");
        ReflectionTestUtils.setField(svc, "readOnly", true);

        var dw = emptyDW();
        ReflectionTestUtils.setField(svc, "dataWrapper", dw);

        var p = new Person();
        p.setFirstName("Monica");
        p.setLastName("Geller");
        p.setAddress("12 Monica St");
        assertThat(svc.addPerson(p)).isTrue();

        var update = new Person();
        update.setAddress("Central Perk");
        update.setCity("New York");
        update.setZip("11111");
        update.setPhone("111-111-1111");
        update.setEmail("monica@gmail.com");
        assertThat(svc.updatePerson("Monica", "Geller", update)).isTrue();

        assertThat(svc.deletePerson("Monica", "Geller")).isTrue();

    }

    // ----------- CRUD: FIRESTATION --------------------

    @Test
    void firestation_add_update_delete_variants() {
        var svc = new DataService();
        ReflectionTestUtils.setField(svc, "dataPath", "classpath:data.json");
        ReflectionTestUtils.setField(svc, "readOnly", true);
        var dw = emptyDW();
        ReflectionTestUtils.setField(svc, "dataWrapper", dw);

        var fs = new Firestation();
        fs.setAddress("12 St");
        fs.setStation("1");
        assertThat(svc.addFirestation(fs)).isTrue();

        assertThat(svc.updateFirestationStation("12 St", "2")).isTrue();

        assertThat(svc.deleteFirestationByAddress("12 St")).isTrue();

        var fs1 = new Firestation();
        fs1.setAddress("A");
        fs1.setStation("5");
        var fs2 = new Firestation();
        fs2.setAddress("B");
        fs2.setStation("5");
        dw.getFirestations().addAll(List.of(fs1, fs2));

        assertThat(svc.deleteFirestationsByStation("5")).isTrue();
    }

    // -------------- CRUD : MEDICAL RECORD ------------------------------

    @Test
    void medicalRecord_add_update_delete_variants() {
        var svc = new DataService();
        ReflectionTestUtils.setField(svc, "dataPath", "classpath:data.json");
        ReflectionTestUtils.setField(svc, "readOnly", true);
        var dw = emptyDW();
        ReflectionTestUtils.setField(svc, "dataWrapper", dw);
        var mr = new MedicalRecord();
        mr.setFirstName("Ross");
        mr.setLastName("Geller");
        mr.setBirthdate("01/01/1980");
        mr.setMedications(List.of("aspirin:100mg"));
        mr.setAllergies(List.of());
        assertThat(svc.addMedicalRecord(mr)).isTrue();

        var update = new MedicalRecord();
        update.setBirthdate("01/01/1980");
        update.setMedications(List.of("newMed:5"));
        update.setAllergies(List.of("cats"));
        assertThat(svc.updateMedicalRecord("Ross", "Geller", update)).isTrue();

        assertThat(svc.deleteMedicalRecord("Ross", "Geller")).isTrue();
    }

}
