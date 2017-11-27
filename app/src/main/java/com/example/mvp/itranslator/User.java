package com.example.mvp.itranslator;

/**
 * Created by MVP on 11/27/2017.
 */

public class User {
    private String userID;
    private String name;
    private String defaultSourceLanguage;
    private String defaultTargetLanguage;
    private String defaultSpeechInputLanguage;

    public User(String userID) {}

    public User(String userID, String name, String defaultSourceLanguage, String defaultTargetLanguage, String defaultSpeechInputLanguage) {
        this.userID = userID;
        this.name = name;
        this.defaultSourceLanguage = defaultSourceLanguage;
        this.defaultTargetLanguage = defaultTargetLanguage;
        this.defaultSpeechInputLanguage = defaultSpeechInputLanguage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultSourceLanguage() {
        return defaultSourceLanguage;
    }

    public void setDefaultSourceLanguage(String defaultSourceLanguage) {
        this.defaultSourceLanguage = defaultSourceLanguage;
    }

    public String getDefaultTargetLanguage() {
        return defaultTargetLanguage;
    }

    public void setDefaultTargetLanguage(String defaultTargetLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
    }

    public String getDefaultSpeechInputLanguage() {
        return defaultSpeechInputLanguage;
    }

    public void setDefaultSpeechInputLanguage(String defaultSpeechInputLanguage) {
        this.defaultSpeechInputLanguage = defaultSpeechInputLanguage;
    }
}
