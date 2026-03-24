package com.escapegame.chl_backend.controller;

import com.escapegame.chl_backend.dto.request.PuzzleSubmissionDTO;
import com.escapegame.chl_backend.dto.response.GameStatusDTO;
import com.escapegame.chl_backend.dto.response.MessageResponse;
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

    /**
     * Le frontend appelle ce endpoint uniquement quand le joueur a fini un énigme.
     * Il envoie l'ID de l'énigme et le temps écoulé.
     */
    @PostMapping("/complete-enigma")
    public ResponseEntity<?> completeEnigma(Authentication authentication,
                                             @RequestBody PuzzleSubmissionDTO request) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            gameService.completeEnigma(userDetails.getUsername(), request);
            return ResponseEntity.ok(new MessageResponse("Énigme complétée et enregistrée."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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