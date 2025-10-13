package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.PersonInfoDTO;
import com.openclassroom.SafetyNet.Alerts.service.PersonInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST pour /personInfo.
 * Retourne les fiches détaillées (âge, médications, allergies) des personnes
 * correspondant à un nom de famille.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PersonInfoController {

    private final PersonInfoService personInfoService;

    @GetMapping("/personInfo")
    public ResponseEntity<List<PersonInfoDTO>> getPersonInfo(@RequestParam("lastName") String lastName) {
        log.info("GET /personInfo?lastName={} - incoming", lastName);
        List<PersonInfoDTO> body = personInfoService.getPersonInfoByLastName(lastName);
        log.info("GET /personInfo - ok: count={}", body.size());
        return ResponseEntity.ok(body);
    }
}
