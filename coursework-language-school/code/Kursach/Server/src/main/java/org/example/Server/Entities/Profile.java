package org.example.Server.Entities;



import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "profile")
public class Profile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "name" , nullable = false)

    private String name;
    @Column(name= "lastname" , nullable = false)

    private String lastname;
    @Column(name= "phonenumber" , nullable = false)
    private String phonenumber;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true,referencedColumnName = "id")
    private User user;

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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
