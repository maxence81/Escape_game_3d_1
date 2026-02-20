package com.escapegame.chl_backend.controller;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.dto.response.GameStatusDTO;
import com.escapegame.chl_backend.dto.response.PuzzleResponseDTO;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.repository.UserRepository;
import com.escapegame.chl_backend.service.GameService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserRepository userRepository;

    // INICIAR NUEVA PARTIDA
    @PostMapping("/start")
    public ResponseEntity<?> startGame(Authentication authentication, @RequestParam String difficulty) {
        // Obtener email del usuario logueado desde el Token
        String email = authentication.getName();
        User player = userRepository.findByEmail(email).orElseThrow();

        // Crear sesión en base de datos
        GameSession session = gameService.startNewSession(player, difficulty);
        
        return ResponseEntity.ok(new GameStatusDTO(session.getId(), "STARTED", session.getStartTime()));
    }

    // VALIDAR UN ENIGMA
    @PostMapping("/validate-puzzle")
    public ResponseEntity<?> validatePuzzle(
            Authentication authentication, 
            @RequestBody PuzzleSubmissionDTO submission) {
        
        // submission trae: { "puzzleId": "room1_safe", "answer": "379" }
        boolean isCorrect = gameService.checkPuzzle(submission.getPuzzleId(), submission.getAnswer());
        
        // Registrar el intento (para estadísticas)
        gameService.recordAttempt(authentication.getName(), submission.getPuzzleId(), isCorrect);

        if (isCorrect) {
            return ResponseEntity.ok(new PuzzleResponseDTO(true, "¡Respuesta Correcta! Acceso concedido."));
        } else {
            return ResponseEntity.badRequest().body(new PuzzleResponseDTO(false, "Respuesta incorrecta."));
        }
    }

    // TERMINAR PARTIDA
    @PostMapping("/end")
    public ResponseEntity<?> endGame(Authentication authentication, @RequestBody int finalScore) {
        String email = authentication.getName();
        GameSession session = gameService.endSession(email, finalScore);
        
        return ResponseEntity.ok(new GameStatusDTO(session.getId(), "COMPLETED", session.getEndTime()));
    }
}