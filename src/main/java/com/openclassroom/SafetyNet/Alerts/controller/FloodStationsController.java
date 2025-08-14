package com.openclassroom.SafetyNet.Alerts.controller;

import com.openclassroom.SafetyNet.Alerts.dto.AddressHouseholdDTO;
import com.openclassroom.SafetyNet.Alerts.service.FloodStationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FloodStationsController {

    private final FloodStationsService floodStationsService;

    @GetMapping("/flood/stations")
    public ResponseEntity<?> getFloodStations(@RequestParam("stations") String stationsParam) {
        log.info("GET /flood/stations?stations={} - incoming", stationsParam);

        if (stationsParam == null || stationsParam.isBlank()) {
            log.error("GET /flood/stations - missing 'stations query parameter");
            return ResponseEntity.ok().body(List.of());
        }

        List<String> stations = Arrays.stream(stationsParam.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        List<AddressHouseholdDTO> body = floodStationsService.getHouseholdsByStations(stations);

        log.info("GET /flood/stations - ok: addresses={}, totalResidents{}",
                body.size(), body.stream().mapToInt(h -> h.getResidents().size()).sum());

        return ResponseEntity.ok(body);
    }
}
