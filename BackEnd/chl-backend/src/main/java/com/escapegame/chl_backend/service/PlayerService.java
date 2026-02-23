package com.escapegame.chl_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.response.EnigmaStatusDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.model.Enigma;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;
import com.escapegame.chl_backend.model.PuzzleAttempt;
import com.escapegame.chl_backend.repository.EnigmaRepository;
import com.escapegame.chl_backend.repository.GameSessionRepository;
import com.escapegame.chl_backend.repository.PuzzleAttemptRepository;
import com.escapegame.chl_backend.repository.UserRepository;

@Service
public class PlayerService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnigmaRepository enigmaRepository;

    @Autowired
    private PuzzleAttemptRepository puzzleAttemptRepository;

    @Autowired
    private AdminService adminService; // Reutilizamos tu código de estadísticas

    /**
     * Obtiene las estadísticas personales del jugador logueado
     */
    public PlayerDetailDTO getMyStats(String email) {
        Player player = (Player) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
                
        // Reutilizamos el método de AdminService pasándole el ID del jugador
        return adminService.getPlayerDetails(player.getId_utilisateur());
    }

    /**
     * Obtiene la lista de enigmas y calcula cuáles están bloqueados o disponibles
     * basado en el progreso del jugador.
     */
    /**
     * Obtiene la lista de enigmas y calcula cuáles están bloqueados o disponibles
     * basado en el progreso del jugador.
     */
    public List<EnigmaStatusDTO> getMyDashboardEnigmas(String email) {
        Player player = (Player) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // 1. Obtener todos los enigmas de la BD
        List<Enigma> allEnigmas = enigmaRepository.findAll();
        
        // 2. Obtener todos los intentos de este jugador de forma segura
        List<GameSession> playerSessions = gameSessionRepository.findByPlayer(player);
        List<PuzzleAttempt> playerAttempts = new ArrayList<>();
        for(GameSession session : playerSessions) {
            playerAttempts.addAll(puzzleAttemptRepository.findBySession(session));
        }

        List<EnigmaStatusDTO> dashboardList = new ArrayList<>();
        
        // Lógica: El primer enigma siempre está disponible. 
        // Los siguientes se desbloquean si el anterior fue "estReussi = true".
        boolean previousCompleted = true; 

        for (Enigma enigma : allEnigmas) {
            EnigmaStatusDTO dto = new EnigmaStatusDTO();
            dto.setId(enigma.getId_enigme());
            dto.setNom(enigma.getNom());
            dto.setDescription(enigma.getDescription());
            dto.setDifficulte(enigma.getDifficulte());

            // Verificar si el jugador ya resolvió este enigma en específico
            boolean isSolved = playerAttempts.stream()
                    .anyMatch(attempt -> 
                        attempt.getEnigma().getId_enigme().equals(enigma.getId_enigme()) 
                        && Boolean.TRUE.equals(attempt.getEstReussi())
                    );

            if (isSolved) {
                dto.setStatus("RÉUSSI"); // Ya lo pasó
                previousCompleted = true; // Permite desbloquear el siguiente
            } else if (previousCompleted) {
                dto.setStatus("COMMENCER"); // Toca jugar este
                previousCompleted = false; // Bloquea los que siguen hasta que pase este
            } else {
                dto.setStatus("VERROUILLÉ"); // Bloqueado
            }

            dashboardList.add(dto);
        }

        return dashboardList;
    }
}
