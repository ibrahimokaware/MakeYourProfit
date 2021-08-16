package com.rivierasoft.makeyourprofit;

public class Level {
    private int image;
    private String title;
    private boolean isOpen;

    public Level(int image, String title, boolean isOpen) {
        this.image = image;
        this.title = title;
        this.isOpen = isOpen;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
