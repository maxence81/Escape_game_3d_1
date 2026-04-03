package com.escapegame.chl_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.response.AdminDashboardStatsDTO;
import com.escapegame.chl_backend.dto.response.EnigmaStatDTO;
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

        // ==========================================================
        // ✅ NUEVO: Calcular el tiempo promedio por enigma
        // ==========================================================
        if (!allAttempts.isEmpty()) {
            // Agrupamos los intentos por el nombre del enigma y promediamos el tiempo
            Map<String, Double> enigmaAverageTimes = allAttempts.stream()
                .filter(a -> a.getEnigma() != null && a.getEnigma().getNom() != null)
                .filter(a -> a.getTempsPasseSec() != null) // Usando tu getter real
                .collect(Collectors.groupingBy(
                    a -> a.getEnigma().getNom(),
                    Collectors.averagingInt(PuzzleAttempt::getTempsPasseSec) // Usando tu getter real
                ));

            // Convertimos el mapa a la lista DTO que espera Vue
            List<EnigmaStatDTO> enigmaStatsList = enigmaAverageTimes.entrySet().stream()
                // Dividimos entre 60.0 porque en BD está en segundos (tempsPasseSec)
                .map(entry -> new EnigmaStatDTO(entry.getKey(), entry.getValue() / 60.0))
                .collect(Collectors.toList());

            stats.setEnigmaStats(enigmaStatsList);
        } else {
            stats.setEnigmaStats(new ArrayList<>());
        }
        // ==========================================================

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

        // Recuperar sesiones e intentos
        List<GameSession> sessions = gameSessionRepository.findByPlayer(player);
        List<PuzzleAttempt> attempts = new ArrayList<>();
        
        int totalTimeSec = 0;
        for (GameSession session : sessions) {
            if (session.getTempsEnLigne() != null) {
                totalTimeSec += session.getTempsEnLigne();
            }
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }
        
        // 1. Asignar Tiempo Total
        detail.setTotalTimeMinutes(totalTimeSec / 60);

        // 2. Calcular enigmas únicos resueltos y total de errores
        long resolvedUnique = attempts.stream()
                .filter(a -> Boolean.TRUE.equals(a.getEstReussi()) && a.getEnigma() != null)
                .map(a -> a.getEnigma().getId_enigme())
                .distinct()
                .count();
        detail.setPuzzlesResolved((int) resolvedUnique);

        long totalErrors = attempts.stream()
                .filter(a -> Boolean.FALSE.equals(a.getEstReussi()))
                .count();
        detail.setTotalErrors((int) totalErrors);

        // 3. Calcular los Skills (Radar)
        Map<String, Integer> skills = new HashMap<>();
        for (SkillType skill : SkillType.values()) {
            skills.put(skill.name(), 50); // Valor base
        }

        for (PuzzleAttempt attempt : attempts) {
            if (attempt.getEnigma() == null || attempt.getEnigma().getCompetence() == null || attempt.getEstReussi() == null) continue;
            String skillName = attempt.getEnigma().getCompetence().name();
            int currentScore = skills.getOrDefault(skillName, 50);

            if (Boolean.TRUE.equals(attempt.getEstReussi())) {
                skills.put(skillName, Math.min(100, currentScore + 10));
            } else {
                skills.put(skillName, Math.max(0, currentScore - 5));
            }
        }
        detail.setSkills(skills);

        // 4. Calcular el tiempo promedio por enigma para la gráfica de barras
        if (!attempts.isEmpty()) {
            Map<String, Double> enigmaAverageTimes = attempts.stream()
                .filter(a -> a.getEnigma() != null && a.getEnigma().getNom() != null)
                .filter(a -> a.getTempsPasseSec() != null)
                .collect(Collectors.groupingBy(
                    a -> a.getEnigma().getNom(),
                    Collectors.averagingInt(PuzzleAttempt::getTempsPasseSec)
                ));

            List<EnigmaStatDTO> enigmaStatsList = enigmaAverageTimes.entrySet().stream()
                .map(entry -> new EnigmaStatDTO(entry.getKey(), entry.getValue() / 60.0))
                .collect(Collectors.toList());

            detail.setEnigmaTimes(enigmaStatsList);
        } else {
            detail.setEnigmaTimes(new ArrayList<>());
        }

        return detail;
    }
}