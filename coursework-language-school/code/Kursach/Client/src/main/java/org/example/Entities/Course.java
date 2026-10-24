package org.example.Entities;

import java.io.Serializable;

public class Course implements Serializable {
    private int id;
    private String course_type;
    private String course_level;

    public Course() {};
    public Course(int id, String course_type, String course_level) {
        this.id = id;
        this.course_type = course_type;
        this.course_level = course_level;
    }
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


}
