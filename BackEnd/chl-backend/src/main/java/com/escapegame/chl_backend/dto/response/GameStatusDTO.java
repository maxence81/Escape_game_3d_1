package com.escapegame.chl_backend.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameStatusDTO {
    private Long sessionId;
    private String status; // "STARTED", "COMPLETED", "FAILED"
    private LocalDateTime timestamp;
}