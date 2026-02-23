package com.escapegame.chl_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escapegame.chl_backend.dto.response.AdminDashboardStatsDTO;
import com.escapegame.chl_backend.dto.response.PlayerDetailDTO;
import com.escapegame.chl_backend.model.Player;
import com.escapegame.chl_backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 1. Obtener Estadísticas Globales del Dashboard
    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden entrar
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = adminService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }

    // 2. Obtener la lista de todos los jugadores (con sus datos de jugador)
    @GetMapping("/players")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = adminService.findAllPlayers();
        return ResponseEntity.ok(players);
    }

    // 3. Obtener el Radar Chart (Competencias) de un jugador específico
    @GetMapping("/player/{id}/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlayerDetailDTO> getPlayerStats(@PathVariable Long id) {
        try {
            PlayerDetailDTO details = adminService.getPlayerDetails(id);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}