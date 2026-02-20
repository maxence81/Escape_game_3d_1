package com.escapegame.chl_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.PuzzleAttempt;

@Repository
public interface PuzzleAttemptRepository extends JpaRepository<PuzzleAttempt, Long> {

    // Obtener todos los intentos de una sesión específica
    // Útil para calcular el puntaje final de una partida
    List<PuzzleAttempt> findBySessionId(Long sessionId);

    // Obtener todos los intentos de un jugador en todas sus partidas
    // Útil para el Gráfico de Radar (SkillType) del Admin
    // Spring hace el JOIN automáticamente: PuzzleAttempt -> GameSession -> User
    List<PuzzleAttempt> findBySession_Player_Id(Long playerId);

    // Obtener todos los intentos de un enigma específico (globalmente)
    // Útil para que el Admin vea: "¿Cuál es el enigma más difícil?"
    List<PuzzleAttempt> findByPuzzleId(String puzzleId);
}