package com.escapegame.chl_backend.dto.request;

import lombok.Data;

@Data
public class PuzzleSubmissionDTO {
    private String puzzleId; // Ej: "room1_safe", "room3_weights"
    private String answer;   // Ej: "379", "LEAOSLO1975"
}