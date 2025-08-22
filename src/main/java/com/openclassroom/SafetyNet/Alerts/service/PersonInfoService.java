package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.dto.PersonInfoDTO;
import com.openclassroom.SafetyNet.Alerts.model.MedicalRecord;
import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class PersonInfoService {

    private final DataService dataService;

    public List<PersonInfoDTO> getPersonInfoByLastName(String lastName) {
        log.info("Calculating /personInfo for lastName '{}'", lastName);

        // indiciser les medical records par first|last
        Map<String, MedicalRecord> mrByKey = dataService.getMedicalrecords().stream()
                .collect(Collectors.toMap(
                        mr -> key(mr.getFirstName(), mr.getLastName()),
                        Function.identity(),
                        (a,b) -> a
                ));

        List<PersonInfoDTO> result = new ArrayList<>();
        for (Person p : dataService.getPersons()) {
            if (lastName.equalsIgnoreCase(p.getLastName())) {
                MedicalRecord mr = mrByKey.get(key(p.getFirstName(), p.getLastName()));
                int age = (mr == null || mr.getBirthdate() == null) ? 200 : calculateAge(mr.getBirthdate());

                PersonInfoDTO dto = new PersonInfoDTO();
                dto.setFirstName(p.getFirstName());
                dto.setLastName(p.getLastName());
                dto.setAddress(p.getAddress());
                dto.setAge(age);
                dto.setEmail(p.getEmail());
                dto.setMedications(mr != null && mr.getMedications() != null ? mr.getMedications() : List.of());
                dto.setAllergies(mr != null && mr.getAllergies() != null ? mr.getAllergies() : List.of());
                result.add(dto);
            }
        }

        log.info("/personInfo '{}': count={}", lastName, result.size());
        return result;
    }

    private static String key(String first, String last) {
        return first + last;
    }

    private int calculateAge(String birthdate) {
        LocalDate birth = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return Period.between(birth, LocalDate.now()).getYears();
    }
}
