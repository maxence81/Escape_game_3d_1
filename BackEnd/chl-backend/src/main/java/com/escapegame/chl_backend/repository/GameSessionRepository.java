package com.escapegame.chl_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    
    // ¡AQUÍ ESTÁ EL SECRETO! Solo deben existir estos dos métodos.
    // Si tenías un findByPlayerId... ¡Bórralo!
    
    List<GameSession> findByPlayer(Player player);
    
    Optional<GameSession> findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(Player player);
}