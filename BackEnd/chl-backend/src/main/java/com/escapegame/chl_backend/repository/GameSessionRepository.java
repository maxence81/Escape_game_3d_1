package com.escapegame.chl_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.GameSession;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    // Buscar todas las partidas de un jugador específico (Para PlayerDashboard)
    List<GameSession> findByPlayerId(Long userId);

    // Buscar si el jugador tiene una partida activa (que aún no tiene fecha de fin)
    // Útil para impedir que inicien dos juegos a la vez
    Optional<GameSession> findByPlayerIdAndEndTimeIsNull(Long userId);

    // Buscar todas las partidas completadas (Para calcular estadísticas globales en Admin)
    List<GameSession> findByIsCompletedTrue();
}