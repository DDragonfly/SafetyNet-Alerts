package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.FireAddressDTO;
import com.openclassroom.SafetyNet.Alerts.service.FireAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FireAddressController {

    private final FireAddressService fireAddressService;

    @GetMapping("/fire")
    public ResponseEntity<?> getFireInfo(@RequestParam("address")  String address) {
        log.info("GET /fire?address={} - incoming", address);

        FireAddressDTO dto = fireAddressService.getFireInfoByAddress(address);

        // si adresse pas trouvé, object vide
        if (dto.getStationNumber() == null || dto.getResidents() == null || dto.getResidents().isEmpty()) {
            log.info("GET /fire - address '{}' not found or no residents -> {}", address, "{}");
            return ResponseEntity.ok(Collections.emptyMap());
        }

        log.info("GET /fire - ok: station={}, residents={}", dto.getStationNumber(), dto.getResidents().size());
        return ResponseEntity.ok(dto);
    }
}
