package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.service.PhoneAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST pour /phoneAlert.
 * Retourne les numéros de téléphone des habitants couverts par une caserne donnée.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PhoneAlertController {

    private final PhoneAlertService phoneAlertService;

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhones(@RequestParam("firestation") String stationNumber) {
        log.info("GET /phoneAlert?firestation={} -incoming", stationNumber);

        // service: liste de numeros uniques
        List<String> phones = phoneAlertService.getPhoneByStation(stationNumber);

        log.info("GET /phoneAlert -ok: count={}", phones.size());
        return ResponseEntity.ok(phones);
    }
}
