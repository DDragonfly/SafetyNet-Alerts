package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResidentAtAddressDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}