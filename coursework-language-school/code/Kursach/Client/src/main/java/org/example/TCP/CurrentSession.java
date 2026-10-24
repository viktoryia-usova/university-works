package org.example.TCP;

public class CurrentSession {
    private static CurrentSession instance;
    private int currentProfileId;
    private String currentRole;
    private int currentStudentId;

    private CurrentSession() {}

    public static CurrentSession getInstance() {
        if (instance == null) {
            instance = new CurrentSession();
        }
        return instance;
    }

    public int getCurrentProfileId() {
        return currentProfileId;
    }

    public void setCurrentProfileId(int id) {
        this.currentProfileId = id;
    }
    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String role) {
        this.currentRole = role;
    }
    public int getCurrentStudentId() {
        return currentStudentId;
    }
    public void setCurrentStudentId(int id) {
        this.currentStudentId = id;
    }
}

