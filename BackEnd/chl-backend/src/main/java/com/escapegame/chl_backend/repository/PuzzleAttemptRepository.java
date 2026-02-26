package com.escapegame.chl_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.PuzzleAttempt;

@Repository
public interface PuzzleAttemptRepository extends JpaRepository<PuzzleAttempt, Long> {
    List<PuzzleAttempt> findBySession(GameSession session);
}