package com.escapegame.chl_backend.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String role;
    private String firstName; // Para decir "Hola, Juan" en el dashboard

    public JwtResponse(String accessToken, Long id, String email, String role, String firstName) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
    }
}
