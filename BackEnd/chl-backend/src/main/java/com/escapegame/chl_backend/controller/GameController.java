package com.escapegame.chl_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.dto.response.GameStatusDTO;
import com.escapegame.chl_backend.dto.response.PuzzleResponseDTO;
import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.service.GameService;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    // 1. Iniciar Partida
    // Ya no lanza excepción si hay sesión activa — GameService la cierra automáticamente
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
    public ResponseEntity<?> validatePuzzle(
            Authentication authentication,
            @RequestBody PuzzleSubmissionDTO request) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean isCorrect = gameService.validatePuzzle(userDetails.getUsername(), request);

            PuzzleResponseDTO response = new PuzzleResponseDTO();
            response.setSuccess(isCorrect);
            response.setMessage(isCorrect
                    ? "Réponse correcte ! Accès accordé."
                    : "Réponse incorrecte.");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Terminar Partida y Guardar tiempo
    // Ahora devuelve 200 incluso si no hay sesión activa (por si el front llama dos veces)
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
            // Si no hay sesión activa, devolver mensaje amigable en vez de 400
            return ResponseEntity.ok("{\"status\":\"NO_ACTIVE_SESSION\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}