package com.example.ringvoip.Home;

public class ChatRoomClass {
    private String Username1;
    private String Username2;
    private String Context;
    private String Datetime;

    public ChatRoomClass() {
    }

    public ChatRoomClass(String username1, String username2, String context, String datetime) {
        Username1 = username1;
        Username2 = username2;
        Context = context;
        Datetime = datetime;
    }

    public String getUsername1() {
        return Username1;
    }

    public void setUsername1(String username1) {
        Username1 = username1;
    }

    public String getUsername2() {
        return Username2;
    }

    public void setUsername2(String username2) {
        Username2 = username2;
    }

    public String getContext() {
        return Context;
    }

    public void setContext(String context) {
        Context = context;
    }

    public String getDatetime() {
        return Datetime;
    }

    public void setDatetime(String datetime) {
        Datetime = datetime;
    }
}
