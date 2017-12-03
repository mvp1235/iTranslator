package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 */

public class Conversation {
    private String firstInput;
    private String secondInput;
    private String firstLanguage;
    private String secondLanguage;
    private String translatedText;

    public Conversation() {}

    public String getFirstInput() {
        return firstInput;
    }

    public void setFirstInput(String firstInput) {
        this.firstInput = firstInput;
    }

    public String getSecondInput() {
        return secondInput;
    }

    public void setSecondInput(String secondInput) {
        this.secondInput = secondInput;
    }

    public String getFirstLanguage() {
        return firstLanguage;
    }

    public void setFirstLanguage(String firstLanguage) {
        this.firstLanguage = firstLanguage;
    }

    public String getSecondLanguage() {
        return secondLanguage;
    }

    public void setSecondLanguage(String secondLanguage) {
        this.secondLanguage = secondLanguage;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
