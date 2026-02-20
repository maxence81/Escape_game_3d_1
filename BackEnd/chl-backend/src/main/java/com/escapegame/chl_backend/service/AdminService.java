package com.escapegame.chl_backend.service;

import com.escapegame.chl_backend.dto.response.AdminDashboardStatsDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.model.*;
import com.escapegame.chl_backend.repository.GameSessionRepository;
import com.escapegame.chl_backend.repository.PuzzleAttemptRepository;
import com.escapegame.chl_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PuzzleAttemptRepository puzzleAttemptRepository;

    /**
     * Calcula las estadísticas globales para el Dashboard principal.
     */
    public AdminDashboardStatsDTO getGlobalStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        // 1. Total de Jugadores
        long totalUsers = userRepository.count();
        stats.setTotalPlayers((int) totalUsers);

        // 2. Calcular Tiempo Promedio y Tasa de Éxito
        List<GameSession> completedSessions = gameSessionRepository.findByIsCompletedTrue();
        
        if (!completedSessions.isEmpty()) {
            double totalSeconds = 0;
            int totalScore = 0;
            
            for (GameSession session : completedSessions) {
                totalSeconds += session.getDurationSeconds();
                totalScore += session.getFinalScore();
            }

            // Promedio de tiempo en minutos
            stats.setAverageTimeMinutes(totalSeconds / completedSessions.size() / 60.0);
            
            // Score promedio (usado como proxy de tasa de éxito)
            stats.setSuccessRate((double) totalScore / completedSessions.size());
        } else {
            stats.setAverageTimeMinutes(0);
            stats.setSuccessRate(0);
        }

        return stats;
    }

    /**
     * Obtiene la lista de todos los jugadores.
     */
    public List<User> findAllPlayers() {
        // Filtramos para no mostrar a otros Admins, solo usuarios
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el perfil de competencias (Radar Chart) de un jugador específico.
     * Analiza sus intentos (PuzzleAttempt) para asignar puntos a Logica, Observación, etc.
     */
    public PlayerDetailDTO getPlayerDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        PlayerDetailDTO detail = new PlayerDetailDTO();
        detail.setPlayerName(user.getFirstName() + " " + user.getLastName());
        detail.setEmail(user.getEmail());

        // Obtener todos los intentos de este jugador
        List<PuzzleAttempt> attempts = puzzleAttemptRepository.findBySession_Player_Id(userId);

        // Mapa para acumular puntajes por habilidad
        // Inicializar en 50 (base)
        Map<String, Integer> skills = new HashMap<>();
        skills.put("LOGIQUE", 50);
        skills.put("OBSERVATION", 50);
        skills.put("RAPIDITE", 50);
        skills.put("COOPERATION", 50);

        for (PuzzleAttempt attempt : attempts) {
            String skillName = attempt.getSkillType() != null ? attempt.getSkillType().name() : "GENERAL";
            
            // Lógica simple de puntuación:
            // Si acertó: +10 puntos a esa habilidad
            // Si falló: -5 puntos
            int currentScore = skills.getOrDefault(skillName, 50);
            
            if (attempt.isSuccess()) {
                skills.put(skillName, Math.min(100, currentScore + 10)); // Max 100
            } else {
                skills.put(skillName, Math.max(0, currentScore - 5)); // Min 0
            }
        }

        detail.setSkills(skills);
        return detail;
    }
}