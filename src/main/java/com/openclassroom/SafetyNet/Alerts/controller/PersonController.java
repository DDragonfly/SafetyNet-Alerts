package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import com.openclassroom.SafetyNet.Alerts.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PersonController {

    @Autowired
    private DataService dataService;

    @GetMapping
    public List<Person> getAllPersons() {
        return dataService.getPersons();
    }
}
