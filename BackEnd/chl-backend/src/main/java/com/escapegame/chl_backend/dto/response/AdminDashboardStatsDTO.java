package com.escapegame.chl_backend.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AdminDashboardStatsDTO {
    private int totalPlayers;
    private double averageTimeMinutes;
    private double successRate; // Porcentaje 0.0 a 1.0 (o 0 a 100)
    
    // ✅ NOUVEAU: Liste pour alimenter le graphique en barres dans Vue
    private List<EnigmaStatDTO> enigmaStats; 
    
    // Optionnel: Si vous souhaitez envoyer des données pour des graphiques supplémentaires
    private Map<String, Integer> ageDistribution; 

    private Map<String, Long> repartitionProfil;
    private Map<String, Long> repartitionAge;
}