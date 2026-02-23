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

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PuzzleAttemptRepository puzzleAttemptRepository;

    public AdminDashboardStatsDTO getGlobalStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();
        long totalPlayers = playerRepository.count();
        stats.setTotalPlayers((int) totalPlayers);

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
            stats.setAverageTimeMinutes(validSessions > 0 ? (totalSeconds / validSessions / 60.0) : 0);
            
            List<PuzzleAttempt> allAttempts = puzzleAttemptRepository.findAll();
            if(!allAttempts.isEmpty()) {
                int totalScore = 0;
                for(PuzzleAttempt attempt : allAttempts) {
                    if (attempt.getScoreFinal() != null) totalScore += attempt.getScoreFinal();
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

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public PlayerDetailDTO getPlayerDetails(Long userId) {
        Player player = playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        PlayerDetailDTO detail = new PlayerDetailDTO();
        detail.setPlayerName(player.getPrenom() + " " + player.getNom());
        detail.setEmail(player.getEmail());

        List<GameSession> sessions = gameSessionRepository.findByPlayer(player);
        List<PuzzleAttempt> attempts = new ArrayList<>();
        for(GameSession session : sessions) {
            // ¡AQUÍ ESTÁ LA MAGIA! Ya no usamos el método problemático
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }

        Map<String, Integer> skills = new HashMap<>();
        skills.put(SkillType.RAPIDITE.name(), 50);
        skills.put(SkillType.PRECISION.name(), 50);
        skills.put(SkillType.LOGIQUE.name(), 50);
        skills.put(SkillType.CREATIVITE.name(), 50);
        skills.put(SkillType.PERSEVERANCE.name(), 50);

        for (PuzzleAttempt attempt : attempts) {
            if (attempt.getEnigma() == null || attempt.getEnigma().getCompetence() == null) continue;
            
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