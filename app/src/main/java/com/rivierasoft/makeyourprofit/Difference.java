package com.rivierasoft.makeyourprofit;

public class Difference {
    private String image;
    private String image_s;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;

    public Difference(String image, String image_s, String choice1, String choice2, String choice3, String choice4) {
        this.image = image;
        this.image_s = image_s;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.choice4 = choice4;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_s() {
        return image_s;
    }

    public void setImage_s(String image_s) {
        this.image_s = image_s;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }
}
