package com.escapegame.chl_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enigmas")
@Data
@NoArgsConstructor
public class Enigma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_enigme;

    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private String reponseAttendue; // La respuesta correcta para validar
    
    private String difficulte;

    @Enumerated(EnumType.STRING)
    private SkillType competence;
}
