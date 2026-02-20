package com.escapegame.chl_backend.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate; // Formato esperado JSON: "YYYY-MM-DD"
}