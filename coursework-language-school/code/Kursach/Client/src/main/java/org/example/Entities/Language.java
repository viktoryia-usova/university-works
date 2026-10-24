package org.example.Entities;

import java.io.Serializable;

public class Language implements Serializable {
    private int id;
    private String languageType;
    public Language(int id){
        this.id = id;
    }
    public Language(int id, String languageType) {
        this.id = id;
        this.languageType = languageType;
    }
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

}
