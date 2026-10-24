package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "feedback")
public class Feedback implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String text;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @Transient
    private String author;
    public Feedback() {}
    public Feedback(String text, User user) {
        this.text = text;
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
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getAuthor() {
        return (user != null) ? user.getUsername() : null;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
