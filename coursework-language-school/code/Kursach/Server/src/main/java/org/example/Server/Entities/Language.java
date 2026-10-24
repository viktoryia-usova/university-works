package org.example.Server.Entities;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name = "language")
public class Language implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "languageType" , nullable = false)
    private String languageType;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getLanguageType() {
        return languageType;
    }
    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }
    public Language(){}
    public Language(String languageType){
        this.languageType = languageType;
    }
}
