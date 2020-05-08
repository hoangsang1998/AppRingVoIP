package com.example.ringvoip.Contact;

public class ContactClass {
    private String name;
    private String sipuri;
    private String status;

    public ContactClass() {
    }

    public ContactClass(String name, String sipuri) {
        this.name = name;
        this.sipuri = sipuri;
    }

    public ContactClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSipuri() {
        return sipuri;
    }

    public void setSipuri(String sipuri) {
        this.sipuri = sipuri;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
