package com.escapegame.chl_backend.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PlayerDetailDTO {
    private String playerName;
    private String email;
    private Map<String, Integer> skills;
    
    // ✅ NUEVOS CAMPOS REALES
    private int puzzlesResolved;
    private int totalTimeMinutes;
    private int totalErrors;
    private List<EnigmaStatDTO> enigmaTimes; // Reutilizamos la clase que creamos antes
}