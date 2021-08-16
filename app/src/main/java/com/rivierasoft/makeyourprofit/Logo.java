package com.rivierasoft.makeyourprofit;

public class Logo {
    private String image;
    private String orientation;
    private String answer;

    public Logo(String image, String orientation, String answer) {
        this.image = image;
        this.orientation = orientation;
        this.answer = answer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
