package com.escapegame.chl_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    // Todas las sesiones de un jugador (para estadísticas)
    List<GameSession> findByPlayer(Player player);

    // Sesión activa (sin fecha de fin) — para validar puzzles y terminar partida
    Optional<GameSession> findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(Player player);

    // Última sesión terminada — para el dashboard de stats
    Optional<GameSession> findFirstByPlayerAndDateFinIsNotNullOrderByDateFinDesc(Player player);
}