package com.escapegame.chl_backend.dto.request;

import lombok.Data;

@Data
public class PuzzleSubmissionDTO {
    private Long enigmaId;         // ID de l'énigme complétée
    private Integer tempsPasseSec; // Temps que le joueur a mis (envoyé par le frontend)
}