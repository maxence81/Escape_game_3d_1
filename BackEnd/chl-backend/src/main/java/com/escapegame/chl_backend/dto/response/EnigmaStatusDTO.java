package com.escapegame.chl_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnigmaStatusDTO {
    private Long id;
    private String nom;
    private String description;
    private String difficulte;
    private String status; // Puede ser: "COMMENCER", "VERROUILLÉ" o "RÉUSSI"
}