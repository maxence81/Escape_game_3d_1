package com.escapegame.chl_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PuzzleResponseDTO {
    private boolean success;
    private String message; // "Código correcto" o "Contraseña incorrecta"
}