package com.rivierasoft.makeyourprofit;

import java.util.ArrayList;

public class LettersQuestion {
    private String image;
    private String text;
    private String answer;
    private ArrayList<String> characters;

    public LettersQuestion(String image, String text, String answer, ArrayList<String> characters) {
        this.image = image;
        this.text = text;
        this.answer = answer;
        this.characters = characters;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<String> characters) {
        this.characters = characters;
    }
}
