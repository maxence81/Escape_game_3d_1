package com.escapegame.chl_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User player;

    private LocalDateTime startTime;
    private LocalDateTime endTime; // Null si la partida está en curso

    private int finalScore; // 0 a 100
    private boolean isCompleted; // true si terminaron el juego, false si se acabó el tiempo

    private String difficulty; // "NORMAL" (todos los enigmas), "FACILE" (3 enigmas)

    // Relación: Una sesión tiene varios intentos de resolución de enigmas
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<PuzzleAttempt> attempts;

    // Método auxiliar para obtener duración en segundos
    public long getDurationSeconds() {
        if (startTime == null || endTime == null) return 0;
        return Duration.between(startTime, endTime).getSeconds();
    }
}