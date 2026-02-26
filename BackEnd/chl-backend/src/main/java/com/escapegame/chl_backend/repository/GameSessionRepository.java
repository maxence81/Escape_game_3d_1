package com.escapegame.chl_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escapegame.chl_backend.model.GameSession;
import com.escapegame.chl_backend.model.Player;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByPlayer(Player player);
    Optional<GameSession> findFirstByPlayerAndDateFinIsNullOrderByDateDebutDesc(Player player);
}