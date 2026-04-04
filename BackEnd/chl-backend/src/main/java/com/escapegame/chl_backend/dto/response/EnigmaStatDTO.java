package com.escapegame.chl_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnigmaStatDTO {
    private String nom;
    private double avgTime; // Tiempo promedio en minutos
    private int erreurs;
    private int score;
}
