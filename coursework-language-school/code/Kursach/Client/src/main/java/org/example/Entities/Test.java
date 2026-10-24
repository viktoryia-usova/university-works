package org.example.Entities;

import java.io.Serializable;

public class Test implements Serializable {
    private int id;
    private int Test_result;
    private Student student;
    private String language_skill;
    private int student_id;
    public Test( Student student,int Test_result, String language_skill) {
        this.student = student;
        this.Test_result = Test_result;
        this.language_skill = language_skill;
    }
    private String lastname;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTest_result() {
        return Test_result;
    }
    public void setTest_result(int Test_result) {
        this.Test_result = Test_result;
    }
    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }
    public String getLanguage_skill() {
        return language_skill;
    }
    public void setLanguage_skill(String language_skill) {
        this.language_skill = language_skill;
    }
    public Test(){}
}
