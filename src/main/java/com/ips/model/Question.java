package com.ips.model;

public class Question {
    private final long id;
    private final String category, difficulty, questionText;
    private final String optionA, optionB, optionC, optionD, correctOption, explanation;

    public Question(long id, String category, String difficulty, String questionText,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctOption, String explanation) {
        this.id=id; this.category=category; this.difficulty=difficulty;
        this.questionText=questionText; this.optionA=optionA; this.optionB=optionB;
        this.optionC=optionC; this.optionD=optionD; this.correctOption=correctOption;
        this.explanation=explanation;
    }
    public long getId(){return id;}
    public String getCategory(){return category;}
    public String getDifficulty(){return difficulty;}
    public String getQuestionText(){return questionText;}
    public String getCorrectOption(){return correctOption;}
    public String getExplanation(){return explanation;}
    public String getOption(String letter) {
        return switch(letter) {
            case "A" -> optionA; case "B" -> optionB;
            case "C" -> optionC; case "D" -> optionD; default -> "";
        };
    }
}
