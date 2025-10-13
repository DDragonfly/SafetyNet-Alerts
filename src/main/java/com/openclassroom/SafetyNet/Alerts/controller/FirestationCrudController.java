package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import com.openclassroom.SafetyNet.Alerts.service.FirestationCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/firestation")
public class FirestationCrudController {

    private final FirestationCrudService firestationCrudService;

    // GET

    @GetMapping("/_all")
    public List<Firestation> all() {
        return firestationCrudService.getAll();
    }

    // POST /firestation -> ajout mapping (address,station)

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Firestation body) {
        if (isBlank(body.getAddress()) || isBlank(body.getStation())) {
            log.error("POST /firestation - missing address/station");
            return ResponseEntity.badRequest().body("address and station are required");
        }
        boolean created = firestationCrudService.create(body);
        if (!created) {
            log.info("POST /firestation - mapping for address '{}' already exists", body.getAddress());
            return ResponseEntity.status(409).body("Mapping for this address already exists");
        }
        return ResponseEntity.status(201).build(); // 201 created
    }

    // PUT /firestation -> mise à jour station d'un adresse

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Firestation body) {
        if (isBlank(body.getAddress()) || isBlank(body.getStation())) {
            log.error("PUT /firestation - missing address/station");
            return ResponseEntity.badRequest().body("address and station are required");
        }
        boolean ok = firestationCrudService.updateStationForAddress(body.getAddress(), body.getStation());
        if (!ok) {
            log.info("PUT /firestation - address '{}'not found",  body.getAddress());
            return ResponseEntity.status(404).body("Address mapping not found");
        }
        return ResponseEntity.ok().build();
    }

    // DELETE /firestation?address ou /firestation?station

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam(required = false) String address,
                                    @RequestParam(required = false) String station) {
        if ((address == null || address.isBlank()) && (station == null || station.isBlank())) {
            return ResponseEntity.badRequest().body("Provide 'address' OR 'station' field");
        }
        if (address != null && !address.isBlank()) {
            boolean ok = firestationCrudService.deleteByAddress(address.trim());
            return ok ? ResponseEntity.noContent().build()
                    : ResponseEntity.status(404).body("Address mapping not found");
        }else {
            boolean ok = firestationCrudService.deleteByStation(station.trim());
            return ok ? ResponseEntity.noContent().build()
                    : ResponseEntity.status(404).body("No mappings for this station");
        }
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
