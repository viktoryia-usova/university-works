package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "course")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "course_type" , nullable = false)
    private String course_type;
    @Column(name = "course_level", nullable = false)
    private String course_level;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCourse_type() {
        return course_type;
    }
    public void setCourse_type(String course_type) {
        this.course_type = course_type;
    }
    public String getCourse_level() {
        return course_level;
    }
    public void setCourse_level(String course_level) {
        this.course_level = course_level;
    }
    public Course() {};
    public Course(String course_type, String course_level) {
        this.course_type = course_type;
        this.course_level = course_level;
    }
}
