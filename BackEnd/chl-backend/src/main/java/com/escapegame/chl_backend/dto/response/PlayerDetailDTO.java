package com.escapegame.chl_backend.dto.response;

import java.util.Map;

import lombok.Data;

@Data
public class PlayerDetailDTO {
    private String playerName;
    private String email;
    
    // Mapa para el Radar Chart: { "LOGIQUE": 80, "OBSERVATION": 60, ... }
    private Map<String, Integer> skills; 
}
