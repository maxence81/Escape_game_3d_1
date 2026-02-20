package com.escapegame.chl_backend.dto.response;

import java.util.Map;

import lombok.Data;

@Data
public class AdminDashboardStatsDTO {
    private int totalPlayers;
    private double averageTimeMinutes;
    private double successRate; // Porcentaje 0.0 a 1.0 (o 0 a 100)
    
    // Opcional: Si quieres enviar datos para gráficos extra
    private Map<String, Integer> ageDistribution; 
}