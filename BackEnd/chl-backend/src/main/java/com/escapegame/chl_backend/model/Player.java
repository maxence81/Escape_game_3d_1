package com.escapegame.chl_backend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "joueurs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Player extends User {
    
    private String prenom;
    private String nom;
    private String pseudo;
    private String etablissement;
    
    private LocalDate dateNaissance;
    
    // Fecha en la que creó la cuenta (Para el mockup de la tabla de usuarios)
    private LocalDate dateInscription;
}
