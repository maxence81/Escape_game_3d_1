package com.escapegame.chl_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.response.AdminDashboardStatsDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;
import com.escapegame.chl_backend.model.PuzzleAttempt;
import com.escapegame.chl_backend.model.SkillType;
import com.escapegame.chl_backend.repository.GameSessionRepository;
import com.escapegame.chl_backend.repository.PlayerRepository;
import com.escapegame.chl_backend.repository.PuzzleAttemptRepository;

@Service
public class AdminService {

    // Ahora usamos PlayerRepository en lugar de UserRepository para los datos específicos
    @Autowired
    private PlayerRepository playerRepository;

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
        long totalPlayers = playerRepository.count();
        stats.setTotalPlayers((int) totalPlayers);

        // 2. Calcular Tiempo Promedio y Tasa de Éxito global
        List<GameSession> allSessions = gameSessionRepository.findAll();
        
        if (!allSessions.isEmpty()) {
            double totalSeconds = 0;
            int validSessions = 0;
            
            for (GameSession session : allSessions) {
                if (session.getTempsEnLigne() != null) {
                    totalSeconds += session.getTempsEnLigne();
                    validSessions++;
                }
            }

            // Promedio de tiempo en minutos
            if(validSessions > 0) {
                stats.setAverageTimeMinutes(totalSeconds / validSessions / 60.0);
            } else {
                stats.setAverageTimeMinutes(0);
            }
            
            // Score promedio (basado en todos los intentos de enigmas)
            List<PuzzleAttempt> allAttempts = puzzleAttemptRepository.findAll();
            if(!allAttempts.isEmpty()) {
                int totalScore = 0;
                for(PuzzleAttempt attempt : allAttempts) {
                    if (attempt.getScoreFinal() != null) {
                        totalScore += attempt.getScoreFinal();
                    }
                }
                stats.setSuccessRate((double) totalScore / allAttempts.size());
            } else {
                stats.setSuccessRate(0);
            }

        } else {
            stats.setAverageTimeMinutes(0);
            stats.setSuccessRate(0);
        }

        return stats;
    }

    /**
     * Obtiene la lista de todos los jugadores (con sus datos como pseudo, fecha, etc).
     */
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Calcula el perfil de competencias (Radar Chart) de un jugador específico.
     * Analiza sus intentos (PuzzleAttempt) cruzados con la tabla Enigma.
     */
    public PlayerDetailDTO getPlayerDetails(Long userId) {
        // Buscamos al jugador por su ID
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        PlayerDetailDTO detail = new PlayerDetailDTO();
        detail.setPlayerName(player.getPrenom() + " " + player.getNom());
        detail.setEmail(player.getEmail());

        // Obtener todas las sesiones de este jugador
        List<GameSession> sessions = gameSessionRepository.findByPlayer(player);
        
        // Juntar todos los intentos de todas sus sesiones
        List<PuzzleAttempt> attempts = new ArrayList<>();
        for(GameSession session : sessions) {
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }

        // Mapa para acumular puntajes por habilidad basados en los Enigmas
        Map<String, Integer> skills = new HashMap<>();
        // Inicializamos las habilidades de tu mockup con 50 puntos base
        skills.put(SkillType.RAPIDITE.name(), 50);
        skills.put(SkillType.PRECISION.name(), 50);
        skills.put(SkillType.LOGIQUE.name(), 50);
        skills.put(SkillType.CREATIVITE.name(), 50);
        skills.put(SkillType.PERSEVERANCE.name(), 50);

        // Procesar los intentos
        for (PuzzleAttempt attempt : attempts) {
            // Si el intento no tiene enigma asignado, lo saltamos
            if (attempt.getEnigma() == null || attempt.getEnigma().getCompetence() == null) {
                continue;
            }
            
            String skillName = attempt.getEnigma().getCompetence().name();
            int currentScore = skills.getOrDefault(skillName, 50);
            
            // Si acertó el enigma, sube esa habilidad. Si falló, baja.
            if (Boolean.TRUE.equals(attempt.getEstReussi())) {
                skills.put(skillName, Math.min(100, currentScore + 10)); // Max 100
            } else {
                skills.put(skillName, Math.max(0, currentScore - 5)); // Min 0
            }
        }

        detail.setSkills(skills);
        return detail;
    }
}