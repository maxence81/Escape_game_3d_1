package com.escapegame.chl_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escapegame.chl_backend.dto.response.EnigmaStatusDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.service.PlayerService;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    // 1. Cargar el "Accueil Post-Inscription" (Lista de Enigmas Bloqueados/Desbloqueados)
    @GetMapping("/dashboard")
    public ResponseEntity<List<EnigmaStatusDTO>> getMyDashboard(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<EnigmaStatusDTO> enigmas = playerService.getMyDashboardEnigmas(email);
            return ResponseEntity.ok(enigmas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 2. Cargar las estadísticas personales del estudiante (Radar Chart)
    @GetMapping("/stats")
    public ResponseEntity<PlayerDetailDTO> getMyStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            PlayerDetailDTO stats = playerService.getMyStats(email);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}