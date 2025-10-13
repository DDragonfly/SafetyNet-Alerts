package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Firestation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirestationCrudService {

    private final DataService dataService;

    public List<Firestation> getAll() {
        return dataService.getFirestations();
    }

    public boolean create(Firestation mapping) {
        log.info("Creating firestation mapping: {} -> station {}", mapping.getAddress(), mapping.getStation());
        return dataService.addFirestation(mapping);
    }

    public boolean updateStationForAddress(String address, String newStation) {
        log.info("Updating station for address {} -> {}", address, newStation);
        return dataService.updateFirestationStation(address, newStation);
    }

    public boolean deleteByAddress(String address) {
        log.info("Deleting firestation mapping by address: {}", address);
        return dataService.deleteFirestationByAddress(address);
    }

    public boolean deleteByStation(String station) {
        log.debug("Current mappings: {}", dataService.getFirestations().size());
        log.info("Deleting firestation mappings by station: {}", station);
        return dataService.deleteFirestationsByStation(station);
    }
}
