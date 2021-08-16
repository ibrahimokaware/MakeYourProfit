package com.rivierasoft.makeyourprofit;

public class Instruction {
    private String text;

    public Instruction(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
