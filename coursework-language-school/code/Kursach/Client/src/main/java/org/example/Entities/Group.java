package org.example.Entities;



import org.example.Server.Entities.Schedule;
import org.example.Server.Entities.Teacher;

import java.io.Serializable;

public class Group implements Serializable {
    private int id;
    private Teacher teacher;
    private Language language;
    private Schedule schedule;
    private Course course;
    public Group() {}
    public Group(int id, Teacher teacher, Language language, Schedule schedule, Course course,String teacherName,String languageName,String scheduleDate,String scheduleTime) {
        this.id = id;
        this.teacher = teacher;
        this.language = language;
        this.schedule = schedule;
        this.course = course;
        this.teacherName = teacherName;
        this.languageName = languageName;
        this.scheduleDate = scheduleDate;
        this.scheduleTime = scheduleTime;

    }
    public Group(int id){
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Teacher getTeacher() {
        return teacher;
    }
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    public Schedule getSchedule() {
        return schedule;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    private String teacherName;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
    private String scheduleDate;
    private String scheduleTime;

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}
