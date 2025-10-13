package com.openclassroom.SafetyNet.Alerts.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO utilisé par l’endpoint /firestation pour représenter :
 * <ul>
 *   <li>le nombre d'adultes et d'enfants couverts</li>
 *   <li>la liste des personnes associées à la caserne</li>
 * </ul>
 */
@Data
public class FirestationCoverageDTO {
    private List<PersonCoveredDTO> persons;
    private int adultCount;
    private int childCount;
}
