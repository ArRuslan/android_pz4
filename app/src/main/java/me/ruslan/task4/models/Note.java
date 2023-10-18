package me.ruslan.task4.models;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String text;
    private String time;
    private int priority;
    private String image;

    public Note(String title, String text, String time, int priority, String image) {
        this.title = title;
        this.text = text;
        this.time = time;
        this.priority = priority;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        title = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public String getTime() {
        return time;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int value) {
        priority = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String value) {
        image = value;
    }

    public File getImageFile() {
        return new File(image);
    }

    public void update(Note note) {
        this.title = note.getTitle();
        this.text = note.getText();
    }
}
