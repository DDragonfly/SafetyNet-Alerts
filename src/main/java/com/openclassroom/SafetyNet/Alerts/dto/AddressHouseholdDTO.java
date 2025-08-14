package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddressHouseholdDTO {
    private String address;
    private List<ResidentAtAddressDTO> residents;
}
