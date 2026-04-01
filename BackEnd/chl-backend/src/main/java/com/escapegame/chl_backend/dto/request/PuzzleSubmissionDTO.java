package com.escapegame.chl_backend.dto.request;

public class PuzzleSubmissionDTO {

    private String puzzleId;
    private String answer;

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
}