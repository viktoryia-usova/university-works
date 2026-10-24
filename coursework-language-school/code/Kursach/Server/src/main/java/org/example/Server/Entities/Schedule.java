package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "schedule")
public class Schedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String date;
    @Column
    private String time;
    @Column
    private String room;


    public Schedule() {}

    public Schedule(String date, String time, String room, int groupa) {
        this.date = date;
        this.time = time;
        this.room = room;
        this.groupa = groupa;
    }
    @Transient
    private int groupa;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getGroupa() {
        return groupa;
    }

    public void setGroupa(int id) {
        this.groupa = id;
    }
}
