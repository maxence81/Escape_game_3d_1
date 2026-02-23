package com.escapegame.chl_backend.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.model.Enigma;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;
import com.escapegame.chl_backend.model.PuzzleAttempt;
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.repository.EnigmaRepository;
import com.escapegame.chl_backend.repository.GameSessionRepository;
import com.escapegame.chl_backend.repository.PuzzleAttemptRepository;
import com.escapegame.chl_backend.repository.UserRepository;

@Service
public class GameService {

    @Autowired private GameSessionRepository sessionRepository;
    @Autowired private PuzzleAttemptRepository attemptRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EnigmaRepository enigmaRepository;

    public GameSession startGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!(user instanceof Player)) {
            throw new RuntimeException("Seuls les joueurs peuvent démarrer une partie.");
        }
        
        Player player = (Player) user;
        sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(player)
                .ifPresent(s -> { throw new RuntimeException("Une session est déjà en cours."); });

        GameSession session = new GameSession();
        session.setPlayer(player);
        session.setDateDebut(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }

    public boolean validatePuzzle(String email, PuzzleSubmissionDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        Long enigmaId = Long.parseLong(request.getPuzzleId());
        Enigma enigma = enigmaRepository.findById(enigmaId)
                .orElseThrow(() -> new RuntimeException("Énigme non trouvée."));

        boolean isCorrect = enigma.getReponseAttendue().equalsIgnoreCase(request.getAnswer());

        PuzzleAttempt attempt = new PuzzleAttempt();
        attempt.setSession(session);
        attempt.setEnigma(enigma);
        attempt.setEstReussi(isCorrect);
        attempt.setScoreFinal(isCorrect ? 100 : 0); 
        attempt.setTempsPasseSec(60); 
        attemptRepository.save(attempt);

        return isCorrect;
    }

    public GameSession endGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository.findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        session.setDateFin(LocalDateTime.now());
        
        long seconds = Duration.between(session.getDateDebut(), session.getDateFin()).getSeconds();
        session.setTempsEnLigne((int) seconds);

        return sessionRepository.save(session);
    }
}