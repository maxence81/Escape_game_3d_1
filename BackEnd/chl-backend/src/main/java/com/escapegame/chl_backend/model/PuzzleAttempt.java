package com.escapegame.chl_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "puzzle_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;

    private String puzzleId; // Identificador único: "room1_wifi", "room3_weights", etc.

    private boolean isSuccess; // true si acertó, false si falló

    private long timeSpentSeconds; // Tiempo que tardó en resolver este enigma específico

    @Enumerated(EnumType.STRING)
    private SkillType skillType; // Habilidad asociada (para el radar chart)
    
    // Opcional: registrar qué respuesta incorrecta dio (útil para debuggear la dificultad)
    private String inputProvided; 
}