package com.escapegame.chl_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    // Busca todas las sesiones de un jugador
    List<GameSession> findByPlayer(Player player);
    
    // Busca la sesión activa de un jugador (la que aún no tiene fecha de fin)
    Optional<GameSession> findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(Player player);
}