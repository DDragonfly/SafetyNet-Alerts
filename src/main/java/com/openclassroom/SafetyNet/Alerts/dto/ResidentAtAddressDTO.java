package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO représentant un résident pour les endpoints /fire et /flood/stations.
 * Contient prénom, nom, âge, téléphone, médications et allergies.
 */
@Data
public class ResidentAtAddressDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}