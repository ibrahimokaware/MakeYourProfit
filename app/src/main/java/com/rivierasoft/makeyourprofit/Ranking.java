package com.rivierasoft.makeyourprofit;

public class Ranking {
    private int number;
    private String name;
    private String points;
    private String photo;

    public Ranking(int number, String name, String points, String photo) {
        this.number = number;
        this.name = name;
        this.points = points;
        this.photo = photo;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
