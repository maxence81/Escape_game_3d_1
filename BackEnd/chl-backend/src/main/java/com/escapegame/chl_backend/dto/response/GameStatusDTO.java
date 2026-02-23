package com.escapegame.chl_backend.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GameStatusDTO {
    private Long sessionId;
    private String status;
    private LocalDateTime timestamp;
}