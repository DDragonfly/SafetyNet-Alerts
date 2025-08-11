package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;
import java.util.List;

@Data
public class FirestationCoverageDTO {
    private List<PersonCoveredDTO> persons;
    private int adultCount;
    private int childCount;
}
