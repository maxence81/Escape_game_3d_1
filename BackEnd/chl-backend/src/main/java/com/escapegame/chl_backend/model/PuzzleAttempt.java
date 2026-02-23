package com.escapegame.chl_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "puzzle_attempts")
@Data
@NoArgsConstructor
public class PuzzleAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_tentative;

    // Relación: A qué sesión pertenece este intento
    @ManyToOne
    @JoinColumn(name = "id_session", nullable = false)
    private GameSession session;

    // Relación: A qué enigma específico están intentando jugar
    @ManyToOne
    @JoinColumn(name = "id_enigme", nullable = false)
    private Enigma enigma;

    private Integer tempsPasseSec;
    private Integer scoreFinal;
    private Boolean estReussi;
}