package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO représentant un foyer couvert par une caserne.
 * Contient l'adresse et la liste des résidents {@link ResidentAtAddressDTO}.
 */
@Data
public class AddressHouseholdDTO {
    private String address;
    private List<ResidentAtAddressDTO> residents;
}
