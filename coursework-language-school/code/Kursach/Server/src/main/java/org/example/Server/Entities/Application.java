package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "application")
public class Application implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "status" , nullable = false)
    private String status;
    @ManyToOne(cascade = CascadeType.MERGE)

    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne
    @JoinColumn(name="type_application_id")
    private Type_application type_application;
    @ManyToOne
    @JoinColumn(name="language_id")
    private Language language;
    @Transient
    private String name;
    @Transient
    private String lastname;
    @Transient
    private String languageName;
    public Application() {}
    public Application(String status, Profile profile, Type_application type_application) {
        this.status = status;
        this.profile = profile;
        this.type_application = type_application;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Profile getProfile() {
        return profile;
    }
    public void setProfile(Profile profile) {
        this.profile = profile;
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
