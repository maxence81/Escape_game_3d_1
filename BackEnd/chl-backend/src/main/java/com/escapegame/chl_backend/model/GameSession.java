package com.escapegame.chl_backend.model;

import java.time.LocalDateTime;

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
@Table(name = "game_sessions")
@Data
@NoArgsConstructor
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_session;

    // Relación: Una sesión pertenece a un solo jugador
    @ManyToOne
    @JoinColumn(name = "id_joueur", nullable = false)
    private Player player;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    // Tiempo total conectado en segundos o minutos
    private Integer tempsEnLigne;
}