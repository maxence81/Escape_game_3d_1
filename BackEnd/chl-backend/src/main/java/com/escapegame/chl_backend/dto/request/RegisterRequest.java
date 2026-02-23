package com.escapegame.chl_backend.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    
    // Nuevos campos del Mockup
    private String prenom;
    private String nom;
    private String pseudo;
    private String etablissement;
    private LocalDate dateNaissance;
}