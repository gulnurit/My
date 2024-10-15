package com.example.myapplication5;



public class Question {
    private String country;
    private String[] options;
    private String correctAnswer;

    public Question(String country, String[] options, String correctAnswer) {
        this.country = country;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getCountry() {
        return country;
    }

    public String[] getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

