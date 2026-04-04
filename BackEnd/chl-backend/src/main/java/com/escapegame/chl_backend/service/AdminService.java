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

        // 1. Total de jugadores
        long totalPlayers = playerRepository.count();
        stats.setTotalPlayers((int) totalPlayers);

        // 2. Tiempo promedio (en minutos) de las sesiones terminadas
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

        // 3. Tasa de éxito global (% de puzzles exitosos sobre total de intentos)
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
        // 4. Calcular el tiempo promedio por enigma para el gráfico de barras
        // ==========================================================
        if (!allAttempts.isEmpty()) {
            // Agrupamos los intentos por el nombre del enigma y promediamos el tiempo
            Map<String, Double> enigmaAverageTimes = allAttempts.stream()
                .filter(a -> a.getEnigma() != null && a.getEnigma().getNom() != null)
                .filter(a -> a.getTempsPasseSec() != null) 
                .collect(Collectors.groupingBy(
                    a -> a.getEnigma().getNom(),
                    Collectors.averagingInt(PuzzleAttempt::getTempsPasseSec) 
                ));

            // Convertimos el mapa a la lista DTO que espera Vue
            // Añadiendo (0, 0) para errores y score ya que no se usan en el dashboard global
            List<EnigmaStatDTO> enigmaStatsList = enigmaAverageTimes.entrySet().stream()
                .map(entry -> new EnigmaStatDTO(entry.getKey(), entry.getValue() / 60.0, 0, 0))
                .collect(Collectors.toList());

            stats.setEnigmaStats(enigmaStatsList);
        } else {
            stats.setEnigmaStats(new ArrayList<>());
        }

        // ==========================================================
        // 📊 NUEVO: REPARTICIÓN POR PUBLICO OBJETIVO Y EDAD
        // ==========================================================
        List<Player> allPlayersList = playerRepository.findAll();
        
        // 1. Agrupar por Perfil (Publico Cible)
        Map<String, Long> profilStats = allPlayersList.stream()
            .filter(p -> p.getProfil() != null)
            .collect(Collectors.groupingBy(Player::getProfil, Collectors.counting()));
        stats.setRepartitionProfil(profilStats);

        // 2. Agrupar por Rango de Edad (Tranche d'âge)
        Map<String, Long> ageStats = new HashMap<>();
        ageStats.put("- de 18 ans", 0L);
        ageStats.put("18 - 20 ans", 0L);
        ageStats.put("21 - 25 ans", 0L);
        ageStats.put("26 ans et +", 0L);

        for (Player p : allPlayersList) {
            if (p.getDateNaissance() != null) {
                // Asumiendo que dateNaissance es un LocalDate
                int age = java.time.Period.between(p.getDateNaissance(), java.time.LocalDate.now()).getYears();
                if (age < 18) {
                    ageStats.put("- de 18 ans", ageStats.get("- de 18 ans") + 1);
                } else if (age <= 20) {
                    ageStats.put("18 - 20 ans", ageStats.get("18 - 20 ans") + 1);
                } else if (age <= 25) {
                    ageStats.put("21 - 25 ans", ageStats.get("21 - 25 ans") + 1);
                } else {
                    ageStats.put("26 ans et +", ageStats.get("26 ans et +") + 1);
                }
            }
        }
        stats.setRepartitionAge(ageStats);

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
        
        for (GameSession session : sessions) {
            attempts.addAll(puzzleAttemptRepository.findBySession(session));
        }
        
        // 1. Calcular el Tiempo Total REAL basado en la suma de los intentos
        int totalTimeSec = attempts.stream()
            .mapToInt(a -> a.getTempsPasseSec() != null ? a.getTempsPasseSec() : 0)
            .sum();
            
        detail.setTotalTimeMinutes(totalTimeSec / 60);

        // 2. Calcular enigmas únicos resueltos
        long resolvedUnique = attempts.stream()
                .filter(a -> Boolean.TRUE.equals(a.getEstReussi()) && a.getEnigma() != null)
                .map(a -> a.getEnigma().getId_enigme())
                .distinct()
                .count();
        detail.setPuzzlesResolved((int) resolvedUnique);

        // 3. Contar errores totales e intentos
        long totalErrors = attempts.stream()
                .filter(a -> Boolean.FALSE.equals(a.getEstReussi()))
                .count();
        detail.setTotalErrors((int) totalErrors);
        
        int totalAttempts = attempts.size();

        // 4. Contar Pistas totales utilizadas
        int totalHints = attempts.stream()
            .mapToInt(a -> a.getIndicesUtilises())
            .sum();

        // ====================================================================
        // 🧠 CÁLCULO INTELIGENTE DEL RADAR DE COMPETENCIAS
        // ====================================================================
        Map<String, Integer> skills = new HashMap<>();

        if (totalAttempts == 0) {
            // Si no ha jugado nada, todo está en 0
            for (SkillType skill : SkillType.values()) {
                skills.put(skill.name(), 0);
            }
        } else {
            // ⚡ RAPIDITÉ: Si termina en < 25 min = 100%. Baja 2% por cada minuto extra.
            int rapidite = 100;
            if (detail.getTotalTimeMinutes() > 25) {
                rapidite = Math.max(20, 100 - ((detail.getTotalTimeMinutes() - 25) * 2));
            }
            skills.put("RAPIDITE", rapidite);

            // 🎯 PRÉCISION: Puzles resueltos vs Intentos totales (Ej: 5 resueltos en 5 intentos = 100%)
            int precision = (int) Math.round(((double) resolvedUnique / totalAttempts) * 100);
            skills.put("PRECISION", precision);

            // 🧩 LOGIQUE: Empieza en 100, pero se penaliza por cada pista utilizada (-10% por pista)
            int logique = Math.max(20, 100 - (totalHints * 10));
            skills.put("LOGIQUE", logique);

            // 🎨 CRÉATIVITÉ: Alta por defecto, pero baja si comete demasiados errores (fuerza bruta)
            int creativite = Math.max(30, 100 - ((int)totalErrors * 5));
            skills.put("CREATIVITE", creativite);

            // 🛡️ PERSÉVÉRANCE: 100% garantizado si termina el juego. Si no, sube a medida que intenta más cosas.
            int perseverance = 50; 
            if (resolvedUnique >= 5) {
                perseverance = 100;
            } else {
                perseverance = Math.min(100, 50 + (totalAttempts * 2));
            }
            skills.put("PERSEVERANCE", perseverance);
        }
        detail.setSkills(skills);

        // ====================================================================
        // 📊 TIEMPO ESPECÍFICO POR ÉNIGME (Para las barras dinámicas)
        // ====================================================================
        List<EnigmaStatDTO> enigmaStatsList = new ArrayList<>();

        if (!attempts.isEmpty()) {
            // Agrupar todos los intentos por el nombre de la sala
            Map<String, List<PuzzleAttempt>> attemptsByEnigma = attempts.stream()
                .filter(a -> a.getEnigma() != null && a.getEnigma().getNom() != null)
                .collect(Collectors.groupingBy(a -> a.getEnigma().getNom()));

            for (Map.Entry<String, List<PuzzleAttempt>> entry : attemptsByEnigma.entrySet()) {
                String roomName = entry.getKey();
                List<PuzzleAttempt> roomAttempts = entry.getValue();

                // Sumar tiempo
                int roomTimeSec = roomAttempts.stream()
                    .mapToInt(a -> a.getTempsPasseSec() != null ? a.getTempsPasseSec() : 0)
                    .sum();
                
                // Contar errores en esta sala
                int roomErrors = (int) roomAttempts.stream()
                    .filter(a -> Boolean.FALSE.equals(a.getEstReussi()))
                    .count();

                // Contar pistas usadas en esta sala
                int roomHints = roomAttempts.stream()
                    .mapToInt(a -> a.getIndicesUtilises())
                    .sum();

                // Comprobar si logró completar la sala al final
                boolean isCompleted = roomAttempts.stream()
                    .anyMatch(a -> Boolean.TRUE.equals(a.getEstReussi()));

                // Calcular Puntaje de esta sala (100 base, -5 por error, -5 por pista)
                int roomScore = 0;
                if (isCompleted) {
                    roomScore = Math.max(0, 100 - (roomErrors * 5) - (roomHints * 5));
                }

                // Guardar la información completa de la sala
                enigmaStatsList.add(new EnigmaStatDTO(roomName, roomTimeSec / 60.0, roomErrors, roomScore));
            }
        }
        detail.setEnigmaTimes(enigmaStatsList);

        return detail;
    }
}