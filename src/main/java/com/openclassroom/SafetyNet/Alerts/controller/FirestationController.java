package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.FirestationCoverageDTO;
import com.openclassroom.SafetyNet.Alerts.service.FirestationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour l'endpoint /firestation.
 * Fournit la couverture (adultes/enfants) et la liste des personnes pour une caserne.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class FirestationController {

    private final FirestationService firestationService;

    /**
     * GET /firestation?stationNumber={n}
     *
     * @param stationNumber numéro de caserne requis
     * @return 200 avec le JSON de couverture; 200 avec des compteurs à 0 si caserne inconnue.
     */
    @GetMapping("/firestation")
    public ResponseEntity<FirestationCoverageDTO> getCoverage(@RequestParam("stationNumber") String stationNumber) {
        log.info("GET /firestation?stationNumber={} - incoming", stationNumber);

        FirestationCoverageDTO body = firestationService.getCoverageByStationNumber(stationNumber);

        // Affiche JSON vide si non trouvé: 200 avec DTO vide
        log.info("GET /firestation - ok: persons={}, adultCount={}, childCount={}",
                body.getPersons() != null ? body.getPersons().size() : 0,
                body.getAdultCount(), body.getChildCount());
        return ResponseEntity.ok().body(body);
    }
}
