package com.example.influencer.UI.Create_Modify_Checkpoint_Menu.SharedComponents.Model;

public class CheckpointThemeItem {

    private int color;
    private int imageResourceId;
    private String text;

    public CheckpointThemeItem(int color, int imageResourceId, String text) {
        this.color = color;
        this.imageResourceId = imageResourceId;
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
