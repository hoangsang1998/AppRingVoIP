package com.example.ringvoip.Chat;

public class ChatMessageClass {
    private String Username;
    private String Context;
    private String Datetime;

    public ChatMessageClass(String username, String context, String datetime, String type) {
        Username = username;
        Context = context;
        Datetime = datetime;
        Type = type;
    }

    public ChatMessageClass(String username, String context, String datetime) {
        Username = username;
        Context = context;
        Datetime = datetime;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    private String Type;

    public ChatMessageClass() {
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
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
