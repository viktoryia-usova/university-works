package org.example.Entities;

import org.example.Entities.Course;

public class Price_list {
    private int id;
    private Course course;
    private int price;
    private String courseName;  // Добавленное поле для хранения названия курса

    // Геттеры и сеттеры для всех полей

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        // Когда устанавливается курс, обновляем его название
        if (course != null) {
            this.courseName = course.getCourse_type();
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
