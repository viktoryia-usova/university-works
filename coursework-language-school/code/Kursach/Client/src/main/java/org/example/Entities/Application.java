package org.example.Entities;

import java.io.Serializable;

public class Application implements Serializable {
    private int id;
    private Profile profile;
    private String status;
    private Type_application type_application;
    private Language language;
    private String name;
    private String lastname;
    private String languageName;

    // Конструктор по умолчанию
    public Application() {}

    // Конструктор с параметрами
    public Application(int id, Profile profile, Type_application type_application, Language language, String status) {
        this.id = id;
        this.profile = profile;
        this.type_application = type_application;
        this.language = language;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Type_application getType_application() {
        return type_application;
    }

    public void setType_application(Type_application type_application) {
        this.type_application = type_application;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLanguageName() {
        return languageName;
    }
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}

