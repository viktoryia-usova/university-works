package org.example.Entities;

import org.example.Server.Entities.Group;

public class Schedule implements java.io.Serializable {
    private int id;
    private String date;
    private String time;
    private String room;
    public Schedule() {}
    public Schedule(String date, String time, String room) {
        this.date = date;
        this.time = time;
        this.room = room;
    }
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
    private Group group;
    public Group getGroup() {
        return group;
    }

    public void setGroup(int id) {
    }
}
