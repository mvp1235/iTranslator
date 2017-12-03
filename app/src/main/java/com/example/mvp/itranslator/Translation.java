package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 */

public class Translation {
    private String inputText;
    private String sourceLanguage;
    private String targetLanguage;
    private String speechLanguage;
    private String translatedText;


    public Translation() {}

    public Translation(String inputText, String sourceLanguage, String targetLanguage, String translatedText, String speechLanguage) {
        this.inputText = inputText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translatedText = translatedText;
        this.speechLanguage = speechLanguage;
    }

    public void swapLanguages() {
        String target = targetLanguage;
        targetLanguage = sourceLanguage;
        sourceLanguage = target;
    }

    public String getSpeechLanguage() {
        return speechLanguage;
    }

    public void setSpeechLanguage(String speechLanguage) {
        this.speechLanguage = speechLanguage;
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}

