package com.escapegame.chl_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- Assurez-vous d'importer ceci

@Data
@AllArgsConstructor
@NoArgsConstructor // <-- ESTA LÍNEA ES LA QUE FALTA
public class PuzzleResponseDTO {
    private boolean success;
    private String message; // "Código correcto" o "Contraseña incorrecta"
}