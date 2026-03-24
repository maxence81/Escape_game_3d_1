package com.escapegame.chl_backend.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.response.AdminDashboardStatsDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.model.*;
import com.escapegame.chl_backend.repository.*;

@Service
public class AdminService {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private GameSessionRepository gameSessionRepository;
    @Autowired private PuzzleAttemptRepository puzzleAttemptRepository;

    public AdminDashboardStatsDTO getGlobalStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        stats.setTotalPlayers((int) playerRepository.count());

        List<GameSession> allSessions = gameSessionRepository.findAll();
        List<GameSession> completedSessions = allSessions.stream()
                .filter(s -> s.getTempsEnLigne() != null)
                .toList();

        if (!completedSessions.isEmpty()) {
            double totalSeconds = completedSessions.stream()
                    .mapToInt(GameSession::getTempsEnLigne)
                    .sum();
            stats.setAverageTimeMinutes(totalSeconds / completedSessions.size() / 60.0);
        } else {
            stats.setAverageTimeMinutes(0);
        }

        // Taux de complétion = sessions terminées / sessions totales
        double completionRate = allSessions.isEmpty() ? 0
                : (double) completedSessions.size() / allSessions.size() * 100.0;
        stats.setSuccessRate(completionRate);

        return stats;
    }

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public PlayerDetailDTO getPlayerDetails(Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Joueur non trouvé"));

        PlayerDetailDTO detail = new PlayerDetailDTO();
        detail.setPlayerName(player.getPrenom() + " " + player.getNom());
        detail.setEmail(player.getEmail());

        List<GameSession> sessions = gameSessionRepository.findByPlayer(player);
        List<PuzzleAttempt> attempts = new ArrayList<>();
        for (GameSession session : sessions) {
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }

        // Scores initiaux à 50 (neutre)
        Map<String, Integer> skills = new HashMap<>();
        for (SkillType skill : SkillType.values()) {
            skills.put(skill.name(), 50);
        }

        // Chaque énigme complétée améliore la compétence liée
        // Le bonus dépend du temps : plus rapide = plus de points
        for (PuzzleAttempt attempt : attempts) {
            if (attempt.getEnigma() == null || attempt.getEnigma().getCompetence() == null) continue;
            if (!Boolean.TRUE.equals(attempt.getEstReussi())) continue;

            String skillName = attempt.getEnigma().getCompetence().name();
            int current = skills.getOrDefault(skillName, 50);

            // Bonus basé sur le temps : < 60s = +15, < 120s = +10, sinon +5
            int bonus = 5;
            if (attempt.getTempsPasseSec() != null) {
                if (attempt.getTempsPasseSec() < 60) bonus = 15;
                else if (attempt.getTempsPasseSec() < 120) bonus = 10;
            }

            skills.put(skillName, Math.min(100, current + bonus));
        }

        detail.setSkills(skills);
        return detail;
    }
}