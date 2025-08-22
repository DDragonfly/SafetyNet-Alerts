package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;

import java.util.List;

@Data
public class PersonInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;
}
