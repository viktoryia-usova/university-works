package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "student")
public class Student implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        @ManyToOne
        @JoinColumn(name="language_id")
        private Language language;
        @ManyToOne
        @JoinColumn(name="group_id")
        private Group group;
        @OneToOne
        @JoinColumn(name="profile_id")
        private Profile profile;
    @Transient
    private String name;
    @Transient
    private String lastname;
    @Transient
    private String phonenumber;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public Language getLanguage() {
            return language;
        }
        public void setLanguage(Language language) {
            this.language = language;
        }
        public Group getGroup() {
            return group;
        }
        public void setGroup(Group group) {
            this.group = group;
        }
        public Profile getProfile() {
            return profile;
        }
        public void setProfile(Profile profile) {
            this.profile = profile;
        }
        public Student( Language language, Group group, Profile profile) {
            this.language = language;
            this.group = group;
            this.profile = profile;
        }
        public Student() {}
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
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    @Transient
    private String languageName;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
    @Transient
    private int groupNumber;
    public int getGroupNumber() {
        return groupNumber;
    }
    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }
    public Student(String name, String lastname, String phonenumber) {
        this.name = name;
        this.lastname = lastname;
        this.phonenumber = phonenumber;
    }

}

