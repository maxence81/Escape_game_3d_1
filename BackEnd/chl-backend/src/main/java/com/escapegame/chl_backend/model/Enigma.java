package com.escapegame.chl_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "enigmas")
public class Enigma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_enigme;

    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String reponseAttendue;

    private String difficulte;

    @Enumerated(EnumType.STRING)
    private SkillType competence;

    public Enigma() {}

    // --- Getters ---

    public Long getId_enigme() {
        return id_enigme;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getReponseAttendue() {
        return reponseAttendue;
    }

    public String getDifficulte() {
        return difficulte;
    }

    public SkillType getCompetence() {
        return competence;
    }

    // --- Setters ---

    public void setId_enigme(Long id_enigme) {
        this.id_enigme = id_enigme;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReponseAttendue(String reponseAttendue) {
        this.reponseAttendue = reponseAttendue;
    }

    public void setDifficulte(String difficulte) {
        this.difficulte = difficulte;
    }

    public void setCompetence(SkillType competence) {
        this.competence = competence;
    }
}