package org.example.Entities;

import java.io.Serializable;

public class Role implements Serializable {
    private int id;
    private String roleType;

    public Role(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getRoleType() {
        return roleType;
    }
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

}
