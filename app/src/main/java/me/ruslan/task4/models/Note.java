package me.ruslan.task4.models;

import android.net.Uri;

public class Note {
    private String title;
    private String text;
    private String time;
    private int priority;
    private Uri image;

    public Note(String title, String text, String time, int priority, Uri image) {
        this.title = title;
        this.text = text;
        this.time = time;
        this.priority = priority;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getPriority() {
        return priority;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri value) {
        image = value;
    }
}
