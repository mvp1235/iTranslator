package com.example.mvp.itranslator;

/**
 * Created by MVP on 11/27/2017.
 * Model object for User instance on database
 */
public class User {
    private String userID;
    private String name;
    private String defaultSourceLanguage;
    private String defaultTargetLanguage;
    private String defaultSpeechInputLanguage;
    private boolean shakeToSpeakEnabled;

    /**
     * Constructor for User
     * @param userID the ID for user
     */
    public User(String userID) {this.userID = userID;}

    /**
     * Constructor for User
     * @param userID the ID for user
     * @param name name of user
     * @param defaultSourceLanguage source language of source text
     * @param defaultTargetLanguage target language for translation
     * @param defaultSpeechInputLanguage speech language for voice input
     */
    public User(String userID, String name, String defaultSourceLanguage, String defaultTargetLanguage, String defaultSpeechInputLanguage) {
        this.userID = userID;
        this.name = name;
        this.defaultSourceLanguage = defaultSourceLanguage;
        this.defaultTargetLanguage = defaultTargetLanguage;
        this.defaultSpeechInputLanguage = defaultSpeechInputLanguage;
        shakeToSpeakEnabled = false;
    }

    /**
     * Check if shake to speak is enabled
     * @return true if enabled, false otherwise
     */
    public boolean shakeToSpeakEnabled() {
        return shakeToSpeakEnabled;
    }

    /**
     * Set shake to speak mode
     * @param shakeToSpeakEnabled value to be set
     */
    public void setShakeToSpeak(boolean shakeToSpeakEnabled) {
        this.shakeToSpeakEnabled = shakeToSpeakEnabled;
    }

    /**
     * Get user ID
     * @return userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Set userID
     * @param userID id to be set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Get user name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set user name
     * @param name name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get default source language
     * @return defaultSourceLanguage
     */
    public String getDefaultSourceLanguage() {
        return defaultSourceLanguage;
    }

    /**
     * Set default source language
     * @param defaultSourceLanguage language to be set
     */
    public void setDefaultSourceLanguage(String defaultSourceLanguage) {
        this.defaultSourceLanguage = defaultSourceLanguage;
    }

    /**
     * Get default target language
     * @return defaultTargetLanguage
     */
    public String getDefaultTargetLanguage() {
        return defaultTargetLanguage;
    }

    /**
     * Set default target language
     * @param defaultTargetLanguage language to be set
     */
    public void setDefaultTargetLanguage(String defaultTargetLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
    }

    /**
     * Get default speech for speech input
     * @return defaultSpeechInputLanguage
     */
    public String getDefaultSpeechInputLanguage() {
        return defaultSpeechInputLanguage;
    }

    /**
     * Set default speech language
     * @param defaultSpeechInputLanguage language to be set
     */
    public void setDefaultSpeechInputLanguage(String defaultSpeechInputLanguage) {
        this.defaultSpeechInputLanguage = defaultSpeechInputLanguage;
    }
}
