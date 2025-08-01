package com.openclassroom.SafetyNet.Alerts.model;

import lombok.Data;

@Data
public class Person
{
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}
