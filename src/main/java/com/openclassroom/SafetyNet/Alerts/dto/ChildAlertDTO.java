package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChildAlertDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<HouseholdMemberDTO> householdMembers;
}
