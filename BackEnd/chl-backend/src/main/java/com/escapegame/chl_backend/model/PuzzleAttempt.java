package com.escapegame.chl_backend.model;

import jakarta.persistence.Column; // ✅ Importante añadir esta importación
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "puzzle_attempts")
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

    private Integer tempsPasseSec;
    private Integer scoreFinal;
    private Boolean estReussi;

    // ✅ NUEVO: Campo para guardar las pistas utilizadas
    @Column(name = "indices_utilises", nullable = false)
    private int indicesUtilises = 0;

    public PuzzleAttempt() {}

    // --- Getters ---

    public Long getId_tentative() {
        return id_tentative;
    }

    public GameSession getSession() {
        return session;
    }

    public Enigma getEnigma() {
        return enigma;
    }

    public Integer getTempsPasseSec() {
        return tempsPasseSec;
    }

    public Integer getScoreFinal() {
        return scoreFinal;
    }

    public Boolean getEstReussi() {
        return estReussi;
    }

    // ✅ NUEVO GETTER
    public int getIndicesUtilises() {
        return indicesUtilises;
    }

    // --- Setters ---

    public void setId_tentative(Long id_tentative) {
        this.id_tentative = id_tentative;
    }

    public void setSession(GameSession session) {
        this.session = session;
    }

    public void setEnigma(Enigma enigma) {
        this.enigma = enigma;
    }

    public void setTempsPasseSec(Integer tempsPasseSec) {
        this.tempsPasseSec = tempsPasseSec;
    }

    public void setScoreFinal(Integer scoreFinal) {
        this.scoreFinal = scoreFinal;
    }

    public void setEstReussi(Boolean estReussi) {
        this.estReussi = estReussi;
    }

    // ✅ NUEVO SETTER
    public void setIndicesUtilises(int indicesUtilises) {
        this.indicesUtilises = indicesUtilises;
    }
}