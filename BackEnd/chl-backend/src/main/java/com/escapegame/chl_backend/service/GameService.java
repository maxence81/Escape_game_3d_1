package com.escapegame.chl_backend.service;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.model.*;
import com.escapegame.chl_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class GameService {

    @Autowired private GameSessionRepository sessionRepository;
    @Autowired private PuzzleAttemptRepository attemptRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EnigmaRepository enigmaRepository;

    // 1. Iniciar Partida
    public GameSession startGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!(user instanceof Player)) {
            throw new RuntimeException("Seuls les joueurs peuvent démarrer une partie.");
        }
        
        Player player = (Player) user;

        // Verificar si ya tiene una sesión activa (sin fecha de fin)
        sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(player)
                .ifPresent(s -> { throw new RuntimeException("Une session est déjà en cours."); });

        GameSession session = new GameSession();
        session.setPlayer(player);
        session.setDateDebut(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }

    // 2. Validar Enigma
    public boolean validatePuzzle(String email, PuzzleSubmissionDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        // Buscar el enigma en la base de datos (convertimos el String del DTO a Long)
        Long enigmaId = Long.parseLong(request.getPuzzleId());
        Enigma enigma = enigmaRepository.findById(enigmaId)
                .orElseThrow(() -> new RuntimeException("Énigme non trouvée."));

        // Validar si la respuesta es correcta
        boolean isCorrect = enigma.getReponseAttendue().equalsIgnoreCase(request.getAnswer());

        // Registrar el intento
        PuzzleAttempt attempt = new PuzzleAttempt();
        attempt.setSession(session);
        attempt.setEnigma(enigma);
        attempt.setEstReussi(isCorrect);
        attempt.setScoreFinal(isCorrect ? 100 : 0); // Lógica básica de puntos
        attempt.setTempsPasseSec(60); // Aquí luego podrías implementar el cálculo real del cronómetro
        
        attemptRepository.save(attempt);

        return isCorrect;
    }

    // 3. Terminar Partida
    public GameSession endGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        session.setDateFin(LocalDateTime.now());
        
        // Calcular tiempo en línea en segundos
        long seconds = Duration.between(session.getDateDebut(), session.getDateFin()).getSeconds();
        session.setTempsEnLigne((int) seconds);

        return sessionRepository.save(session);
    }
}