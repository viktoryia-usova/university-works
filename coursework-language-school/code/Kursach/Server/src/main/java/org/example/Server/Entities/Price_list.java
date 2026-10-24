package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "prise_list")
public class Price_list implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private int price;
    @OneToOne
    @JoinColumn(name = "course_id", unique = true, referencedColumnName = "id", nullable = false)
    private Course course;
    public Price_list() {}
    public Price_list(int price, Course course) {
        this.price = price;
        this.course = course;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }


    public void setCourseName(String courseName) {
        if (this.course != null) {
            this.course.setCourse_type(courseName);
        }
    }
    public String getCourseName() {
        return (course != null) ? course.getCourse_type() : null;
    }

}
