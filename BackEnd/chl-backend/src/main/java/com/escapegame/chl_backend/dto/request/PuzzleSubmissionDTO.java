package com.escapegame.chl_backend.dto.request;

public class PuzzleSubmissionDTO {

    private String puzzleId;
    private String answer;

    // ✅ NUEVOS CAMPOS: El frontend enviará esta información
    private int timeSpentSeconds; 
    private int hintsUsed;

    public PuzzleSubmissionDTO() {}

    public String getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

    public void setTimeSpentSeconds(int timeSpentSeconds) {
        this.timeSpentSeconds = timeSpentSeconds;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }
}