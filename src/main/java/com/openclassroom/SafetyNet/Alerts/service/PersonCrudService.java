package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonCrudService {
    private final DataService dataService;

    public List<Person> getAll() {
        return dataService.getPersons();
    }

    public boolean create(Person person) {
        log.info("Creating person {} {}", person.getFirstName(), person.getLastName());
        return dataService.addPerson(person);
    }

    public boolean update(String firstName, String lastName, Person updates) {
        log.info("Updating person {} {}", firstName, lastName);
        return dataService.updatePerson(firstName, lastName, updates);
    }

    public boolean delete(String firstName, String lastName) {
        log.info("Deleting person {} {}", firstName, lastName);
        return dataService.deletePerson(firstName, lastName);
    }
}
