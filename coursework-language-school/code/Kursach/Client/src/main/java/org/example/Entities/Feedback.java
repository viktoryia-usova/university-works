package org.example.Entities;

import java.io.Serializable;

public class Feedback implements Serializable {
    private int id;
    private String text;
    private String author;
    private User user;
    public Feedback(String text, User user) {
        this.text = text;
        this.user = user;
    }
    public Feedback(String text, String author, User user) {
        this.text = text;
        this.author = author;
        this.user = user;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;

    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
