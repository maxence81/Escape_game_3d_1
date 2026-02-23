package com.escapegame.chl_backend.controller;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.dto.response.GameStatusDTO;
import com.escapegame.chl_backend.dto.response.PuzzleResponseDTO;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    // 1. Iniciar Partida
    @PostMapping("/start")
    public ResponseEntity<?> startGame(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            GameSession session = gameService.startGame(userDetails.getUsername());

            GameStatusDTO status = new GameStatusDTO();
            status.setSessionId(session.getId_session());
            status.setStatus("STARTED");
            status.setTimestamp(session.getDateDebut());

            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Validar Respuesta a un Enigma
    @PostMapping("/validate-puzzle")
    public ResponseEntity<?> validatePuzzle(Authentication authentication, @RequestBody PuzzleSubmissionDTO request) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean isCorrect = gameService.validatePuzzle(userDetails.getUsername(), request);

            PuzzleResponseDTO response = new PuzzleResponseDTO();
            response.setSuccess(isCorrect);
            response.setMessage(isCorrect ? "¡Respuesta Correcta! Acceso concedido." : "Respuesta incorrecta. Intenta de nuevo.");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Terminar Partida y Guardar tiempo
    @PostMapping("/end")
    public ResponseEntity<?> endGame(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            GameSession session = gameService.endGame(userDetails.getUsername());

            GameStatusDTO status = new GameStatusDTO();
            status.setSessionId(session.getId_session());
            status.setStatus("COMPLETED");
            status.setTimestamp(session.getDateFin());

            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}