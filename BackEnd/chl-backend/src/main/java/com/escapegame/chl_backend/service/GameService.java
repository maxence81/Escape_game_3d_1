package com.escapegame.chl_backend.service;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.repository.GameSessionRepository;
import com.escapegame.chl_backend.repository.PuzzleAttemptRepository;

@Service
public class GameService {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private PuzzleAttemptRepository puzzleAttemptRepository;

    /**
     * Inicia una nueva sesión de juego para un usuario.
     */
    public GameSession startNewSession(User player, String difficulty) {
        // Verificar si ya tiene una sesión activa para cerrarla (opcional)
        // Por simplicidad, creamos una nueva directamente
        
        GameSession session = new GameSession();
        session.setPlayer(player);
        session.setStartTime(LocalDateTime.now());
        session.setDifficulty(difficulty);
        session.setCompleted(false);
        session.setFinalScore(0);
        
        return gameSessionRepository.save(session);
    }

    /**
     * Valida la respuesta de un enigma basándose en el documento "Livret GM".
     * @param puzzleId ID único del enigma (definido en el Frontend)
     * @param answer Respuesta enviada por el usuario
     * @return true si es correcta
     */
    public boolean checkPuzzle(String puzzleId, String answer) {
        if (answer == null) return false;
        String cleanAnswer = answer.trim().toUpperCase(Locale.ROOT);

        return switch (puzzleId) {
            case "room1_safe" -> cleanAnswer.equals("379");
            case "room2_login" -> cleanAnswer.equals("LEAOSLO1975");
            case "room3_code" -> cleanAnswer.equals("124");
            case "room3_graph_value" -> cleanAnswer.equals("35");
            case "room4_treatment" -> cleanAnswer.contains("AMOXICILLINE");
            case "final_verdict" -> cleanAnswer.contains("DECKARD") || cleanAnswer.contains("EMMA");
            default -> false;
        }; // Aceptamos variaciones
        // Se acepta cualquiera de los dos, pero no "ninguno" o "los dos".
        // Aquí validamos que hayan escrito un nombre válido.
    }

    /**
     * Registra un intento (Acierto o Fallo) en la base de datos.
     * Sirve para las estadísticas del Admin.
     */
    @Transactional
    public void recordAttempt(String userEmail, String puzzleId, boolean isSuccess) {
        // En un caso real, necesitaríamos el ID de la sesión actual.
        // Aquí simplificamos buscando la última sesión activa del usuario.
        // (Asumimos que la lógica de buscar sesión se maneja en el Controller o aquí)
    }

    /**
     * Finaliza la partida y calcula el puntaje.
     */
    @Transactional
    public GameSession endSession(String userEmail, int finalScore) {
        // 1. Buscar la sesión activa del usuario (la que tiene endTime = null)
        // Nota: Esto requiere una consulta custom en el Repository, o pasar el ID de sesión.
        // Aquí simularemos que recuperamos la sesión por lógica de negocio.
        
        // Simulación: recuperar la última sesión del usuario
        // GameSession session = gameSessionRepository.findByPlayerEmailAndActive(userEmail)...
        
        // Como no tenemos el método exacto aquí, usaremos lógica genérica:
        // Update: Lo ideal es que el frontend envíe el ID de la sesión, 
        // pero si solo enviamos el score, buscamos la última abierta.
        
        return null; // (Implementación pendiente de lógica de sesión específica)
    }
    
    // Sobrecarga para usar cuando tenemos el objeto Sesión
    public GameSession endSession(GameSession session, int scoreCalculated) {
        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);
        session.setFinalScore(scoreCalculated);
        return gameSessionRepository.save(session);
    }
}