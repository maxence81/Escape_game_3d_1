package com.escapegame.chl_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.PuzzleAttempt;

public interface PuzzleAttemptRepository extends JpaRepository<PuzzleAttempt, Long> {
    // Busca todos los intentos de una sesión específica
    List<PuzzleAttempt> findBySession(GameSession session);

    
}