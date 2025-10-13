package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.ChildAlertDTO;
import com.openclassroom.SafetyNet.Alerts.service.ChildAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChildAlertController {

    private final ChildAlertService childAlertService;

    @GetMapping("/childAlert")
    public ResponseEntity<?> getChildAlert(@RequestParam("address") String address) {
        log.info("GET /childAlert?address={} - incoming", address);

        if (address == null || address.isBlank()) {
            log.error("GET /childAlert - missing 'address' parameter");
        }

        List<ChildAlertDTO> children = childAlertService.getChildrenByAddress(address);

        if (children.isEmpty()) {
            // info pour la châine vide si pas d'enfants
            log.info("GET /childAlert - no children found at '{}'", address);
            return ResponseEntity.ok("");
        }

        log.info("GET /childAlert - ok: children={}", children.size());
        return ResponseEntity.ok(children);
    }
}
