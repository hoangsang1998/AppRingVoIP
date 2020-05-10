package com.example.ringvoip.Login;

public class UserClass {
    private String Username;
    private String Sipuri;
    private String Status;

    public UserClass() {
    }

    public UserClass(String username, String sipuri, String status) {
        Username = username;
        Sipuri = sipuri;
        Status = status;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getSipuri() {
        return Sipuri;
    }

    public void setSipuri(String sipuri) {
        Sipuri = sipuri;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
