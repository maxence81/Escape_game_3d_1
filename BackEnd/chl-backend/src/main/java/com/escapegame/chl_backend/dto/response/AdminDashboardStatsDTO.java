package com.escapegame.chl_backend.dto.response;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AdminDashboardStatsDTO {
    private int totalPlayers;
    private double averageTimeMinutes;
    private double successRate; // Porcentaje 0.0 a 1.0 (o 0 a 100)
    
    // ✅ NUEVO: Lista para alimentar el gráfico de barras en Vue
    private List<EnigmaStatDTO> enigmaStats; 
    
    // Opcional: Si quieres enviar datos para gráficos extra
    private Map<String, Integer> ageDistribution; 
}