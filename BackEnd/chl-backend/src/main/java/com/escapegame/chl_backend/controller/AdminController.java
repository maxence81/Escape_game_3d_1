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
import com.escapegame.chl_backend.model.User;
import com.escapegame.chl_backend.service.AdminService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Solo usuarios con rol ADMIN pueden acceder aquí
public class AdminController {

    @Autowired
    private AdminService adminService;

    // DATOS PARA EL DASHBOARD PRINCIPAL
    @GetMapping("/dashboard-stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = adminService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }

    // LISTA DE JUGADORES
    @GetMapping("/players")
    public ResponseEntity<List<User>> getAllPlayers() {
        List<User> players = adminService.findAllPlayers();
        return ResponseEntity.ok(players);
    }

    // DETALLE DE UN JUGADOR ESPECÍFICO (Gráfico Radar)
    @GetMapping("/player/{id}")
    public ResponseEntity<PlayerDetailDTO> getPlayerStats(@PathVariable Long id) {
        PlayerDetailDTO playerStats = adminService.getPlayerDetails(id);
        return ResponseEntity.ok(playerStats);
    }
}