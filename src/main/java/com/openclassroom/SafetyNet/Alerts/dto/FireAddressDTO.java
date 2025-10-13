package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;
import java.util.List;

@Data
public class FireAddressDTO {
    private String stationNumber;
    private List<ResidentAtAddressDTO> residents;
}
