package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "test")
public class Test implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private int Test_result;
    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @Column
    private String language_skill;
    @Transient
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
    public void setTest_result(int test_result) {
        Test_result = test_result;
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
    public Test(int id, int Test_result, Student student, String language_skill) {
        this.id = id;
        this.Test_result = Test_result;
        this.student = student;
        this.language_skill = language_skill;
    }
}
