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

        // Si hay sesión activa, cerrarla antes de crear una nueva
        sessionRepository
            .findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(player)
            .ifPresent(s -> {
                s.setDateFin(LocalDateTime.now());
                long seconds = Duration.between(s.getDateDebut(), s.getDateFin()).getSeconds();
                s.setTempsEnLigne((int) seconds);
                sessionRepository.save(s);
            });

        GameSession session = new GameSession();
        session.setPlayer(player);
        session.setDateDebut(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public boolean validatePuzzle(String email, PuzzleSubmissionDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository
                .findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        // Recuperar puzzleId y answer directamente desde los campos del DTO
        String puzzleIdStr = request.getPuzzleId();
        String answer = request.getAnswer();

        Long enigmaId;
        try {
            enigmaId = Long.valueOf(puzzleIdStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("ID d'énigme invalide: " + puzzleIdStr);
        }

        Enigma enigma = enigmaRepository.findById(enigmaId)
                .orElseThrow(() -> new RuntimeException("Énigme non trouvée."));

        // Determinar si es correcto
        boolean isCorrect;
        if ("SUCCESS".equalsIgnoreCase(answer)) {
            isCorrect = true;
        } else if ("FAIL".equalsIgnoreCase(answer)) {
            isCorrect = false;
        } else {
            // Comparación con la respuesta esperada del enigma
            String reponseAttendue = enigma.getReponseAttendue();
            isCorrect = reponseAttendue != null && reponseAttendue.equalsIgnoreCase(answer);
        }

        // Calcular tiempo pasado desde el inicio de la sesión
        int tempsPasseSec = (int) Duration.between(
            session.getDateDebut(),
            LocalDateTime.now()
        ).getSeconds();

        // Crear el intento — usar Integer.valueOf() para evitar autoboxing ambiguo
        PuzzleAttempt attempt = new PuzzleAttempt();
        attempt.setSession(session);
        attempt.setEnigma(enigma);
        attempt.setEstReussi(isCorrect);
        attempt.setScoreFinal(isCorrect ? 100 : 0);
        attempt.setTempsPasseSec(tempsPasseSec);
        attemptRepository.save(attempt);

        return isCorrect;
    }

    public GameSession endGame(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        GameSession session = sessionRepository
                .findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc((Player) user)
                .orElseThrow(() -> new RuntimeException("Aucune session active trouvée."));

        session.setDateFin(LocalDateTime.now());

        long seconds = Duration.between(session.getDateDebut(), session.getDateFin()).getSeconds();
        session.setTempsEnLigne((int) seconds);

        return sessionRepository.save(session);
    }
}