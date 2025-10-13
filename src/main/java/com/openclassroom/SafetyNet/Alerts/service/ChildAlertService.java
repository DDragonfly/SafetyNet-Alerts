package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.ChildAlertDTO;
import com.openclassroom.SafetyNet.Alerts.dto.HouseholdMemberDTO;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChildAlertService {

    @Autowired
    private DataService dataService;

    public List<ChildAlertDTO> getChildrenByAddress(String address) {
        // Prends toutes les residents á cette addresse
        List<Person> residents = dataService.getPersons().stream()
                .filter(p -> address.equalsIgnoreCase(p.getAddress()))
                .toList();

        if (residents.isEmpty()) {
            log.info("No residents found at address {}", address);
            return List.of();
        }

        // Map avec nom et prenom et medical record
        Map<String, MedicalRecord> mrByName = dataService.getMedicalrecords().stream()
                .collect(Collectors.toMap(
                        mr -> key(mr.getFirstName(), mr.getLastName()),
                        Function.identity(),
                        (a, b) -> a // si dupicates, tiens le premier
                ));

        // division entre enfants (<18) et adultes
        List<Person> children = new ArrayList<>();
        List<Person> otherMembers = new ArrayList<>();

        for (Person p : residents) {
            int age = ageOf(p, mrByName);
            if (age < 18) {
                children.add(p);
            } else {
                otherMembers.add(p);
            }
        }

        if (children.isEmpty()) {
            log.info("No children found at address {}", address);
            return List.of();
        }

        // DTO de chaque enfant et liste autres membres
        List<ChildAlertDTO> result = new ArrayList<>();
        for (Person child : children) {
            int age = ageOf(child, mrByName);

            // household -> toutes les residents sauf l'enfant
            List<HouseholdMemberDTO> household = residents.stream()
                    .filter(p -> !(p.getFirstName().equals(child.getFirstName())
                        && p.getLastName().equals(child.getLastName())))
                    .map(p -> {
                        HouseholdMemberDTO hm = new HouseholdMemberDTO();
                        hm.setFirstName(p.getFirstName());
                        hm.setLastName(p.getLastName());
                        return hm;
                    })
                    .toList();

            ChildAlertDTO dto = new ChildAlertDTO();
            dto.setFirstName(child.getFirstName());
            dto.setLastName(child.getLastName());
            dto.setAge(age);
            dto.setHouseholdMembers(household);
            result.add(dto);
        }

        log.info("ChildAlert at '{}': children={}, householdSize(each varies)", address, result.size());
        return result;
    }

    private static String key(String first, String last) {
        return first + "|" + last;
    }

    // Calcul âge (MM/dd/yyyy). Si medical record manque, metons la personne dans les adultes
    private int ageOf(Person p, Map<String, MedicalRecord> mrByName) {
        MedicalRecord mr = mrByName.get(key(p.getFirstName(), p.getLastName()));
        if (mr == null || mr.getBirthdate() == null) {
            return 200;
        }
        LocalDate birthDate = LocalDate.parse(mr.getBirthdate(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
