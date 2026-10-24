package org.example.Entities;




import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class Profile implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("name")
    private String name;
    @SerializedName("phonenumber")
    private String phonenumber;
    @SerializedName("user")
    private User user;

    public Profile() {}

    public Profile(int id, String lastname, String name, String phonenumber, User user) {
        this.id = id;
        this.lastname = lastname;
        this.name = name;
        this.phonenumber = phonenumber;
        this.user = user;
    }
    public Profile(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", name='" + name + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", user=" + (user != null ? user.getUsername() : "No User") +
                '}';
    }

}
