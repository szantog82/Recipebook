package com.example.szantog.recipebook.models;

import android.util.Log;

import java.io.Serializable;

public class ChatItem implements Serializable {

    private Long time;
    private String from;
    private String message;

    public ChatItem(Long time, String from, String message) {
        this.time = time;
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public Long getTime() {
        return time;
    }
}
