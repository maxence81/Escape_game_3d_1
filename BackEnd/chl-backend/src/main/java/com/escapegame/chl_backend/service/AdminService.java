package com.escapegame.chl_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PuzzleAttemptRepository puzzleAttemptRepository;

    /**
     * Estadísticas globales para el dashboard del admin
     */
    public AdminDashboardStatsDTO getGlobalStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        // Total de jugadores
        long totalPlayers = playerRepository.count();
        stats.setTotalPlayers((int) totalPlayers);

        // Tiempo promedio (en minutos) de las sesiones terminadas
        List<GameSession> allSessions = gameSessionRepository.findAll();
        List<GameSession> completedSessions = allSessions.stream()
                .filter(s -> s.getTempsEnLigne() != null && s.getTempsEnLigne() > 0)
                .collect(Collectors.toList());

        if (!completedSessions.isEmpty()) {
            double totalSeconds = completedSessions.stream()
                    .mapToInt(GameSession::getTempsEnLigne)
                    .sum();
            stats.setAverageTimeMinutes(totalSeconds / completedSessions.size() / 60.0);
        } else {
            stats.setAverageTimeMinutes(0);
        }

        // Tasa de éxito global (% de puzzles exitosos sobre total de intentos)
        List<PuzzleAttempt> allAttempts = puzzleAttemptRepository.findAll();
        if (!allAttempts.isEmpty()) {
            long successCount = allAttempts.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getEstReussi()))
                    .count();
            stats.setSuccessRate((double) successCount / allAttempts.size() * 100.0);
        } else {
            stats.setSuccessRate(0);
        }

        return stats;
    }

    /**
     * Lista de todos los jugadores
     */
    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Detalle de un jugador: nombre, email y radar de competencias
     */
    public PlayerDetailDTO getPlayerDetails(Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Joueur non trouvé"));

        PlayerDetailDTO detail = new PlayerDetailDTO();
        detail.setPlayerName(player.getPrenom() + " " + player.getNom());
        detail.setEmail(player.getEmail());

        // Recuperar todos los intentos del jugador
        List<GameSession> sessions = gameSessionRepository.findByPlayer(player);
        List<PuzzleAttempt> attempts = new ArrayList<>();
        for (GameSession session : sessions) {
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }

        // Inicializar todas las competencias a 50 (valor neutro)
        Map<String, Integer> skills = new HashMap<>();
        for (SkillType skill : SkillType.values()) {
            skills.put(skill.name(), 50);
        }

        // Ajustar según los intentos: +10 si correcto, -5 si incorrecto
        for (PuzzleAttempt attempt : attempts) {
            // Verificación de nulos para evitar NullPointerException
            if (attempt.getEnigma() == null) continue;
            if (attempt.getEnigma().getCompetence() == null) continue;
            if (attempt.getEstReussi() == null) continue;

            String skillName = attempt.getEnigma().getCompetence().name();
            int currentScore = skills.getOrDefault(skillName, 50);

            if (Boolean.TRUE.equals(attempt.getEstReussi())) {
                skills.put(skillName, Math.min(100, currentScore + 10));
            } else {
                skills.put(skillName, Math.max(0, currentScore - 5));
            }
        }

        detail.setSkills(skills);
        return detail;
    }
}