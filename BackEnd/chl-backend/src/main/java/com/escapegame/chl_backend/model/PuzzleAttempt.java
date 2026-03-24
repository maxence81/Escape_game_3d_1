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

    @ManyToOne
    @JoinColumn(name = "id_session", nullable = false)
    private GameSession session;

    @ManyToOne
    @JoinColumn(name = "id_enigme", nullable = false)
    private Enigma enigma;

    private Integer tempsPasseSec; // Temps envoyé par le frontend
    private Boolean estReussi;     // Toujours true (le frontend envoie uniquement si complété)
}