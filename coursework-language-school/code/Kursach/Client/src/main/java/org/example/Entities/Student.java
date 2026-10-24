package org.example.Entities;




public class Student implements java.io.Serializable {
    private int id;
    private Language language;
    private Group group;
    private Profile profile;
    private String name;
    private String lastname;
    private String phonenumber;
    private String languageName;
    private int groupNumber;
    public Student() {}
    public Student(int id, Language language, Group group, Profile profile) {
        this.id = id;
        this.language = language;
        this.group = group;
        this.profile = profile;
    }

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
    public String getLanguageName() {
        return languageName;
    }
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
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
