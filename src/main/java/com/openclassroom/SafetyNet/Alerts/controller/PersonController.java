package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.service.PersonCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonCrudService personCrudService;

    // GET /person -> all persons
    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> all = personCrudService.getAll();
        return ResponseEntity.ok(all);
    }

    // POST /person creer une nouvelle personne
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Person body) {
        if (isBlank(body.getFirstName()) || isBlank(body.getLastName())) {
            return ResponseEntity.badRequest().body("firstName and last name are required");
        }
        boolean created = personCrudService.create(body);
        if (!created) {
            return ResponseEntity.status(409).body("Person already exists");
        }
        return ResponseEntity.status(201).build(); // 201 is created
    }

    // PUT update champs
    @PutMapping
    public ResponseEntity<?> update(@RequestParam String firstName, @RequestParam String lastName, @RequestBody Person updates) {
        boolean ok = personCrudService.update(firstName, lastName, updates);
        if (!ok) {
            return ResponseEntity.status(404).body("Person not found");
        }
        return ResponseEntity.ok().build();
    }

    // DELETE
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String firstName, @RequestParam String lastName) {
        boolean ok = personCrudService.delete(firstName, lastName);
        if (!ok) {
            return ResponseEntity.status(404).body("Person not found");
        }
        return ResponseEntity.noContent().build(); // 204
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
