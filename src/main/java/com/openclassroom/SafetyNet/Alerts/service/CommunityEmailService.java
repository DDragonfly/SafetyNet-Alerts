package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityEmailService {

    private final DataService dataService;

    public List<String> getEmailsByCity(String city) {
        log.info("Calculating /communityEmail for city '{}'", city);
        Set<String> emails = new TreeSet<>();
        for (Person p : dataService.getPersons()) {
            if (city.equalsIgnoreCase(p.getCity()) && p.getEmail() != null && !p.getEmail().isBlank()) {
                emails.add(p.getEmail());
            }
        }
        log.info("/communityEmail '{}': count={}", city, emails.size());
        return List.copyOf(emails);
    }
}
