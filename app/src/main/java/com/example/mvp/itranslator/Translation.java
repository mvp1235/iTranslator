package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 * Model object for Translation activity, including an input text, source, target, and speech language, and translated text
 */
public class Translation {
    private String inputText;
    private String sourceLanguage;
    private String targetLanguage;
    private String speechLanguage;
    private String translatedText;

    /**
     * Empty constructor for Translation
     */
    public Translation() {}

    /**
     * Constructor for Translation
     * @param inputText text to be translated
     * @param sourceLanguage language of inputText
     * @param targetLanguage language to be translated to
     * @param translatedText translated result
     * @param speechLanguage language for voice input
     */
    public Translation(String inputText, String sourceLanguage, String targetLanguage, String translatedText, String speechLanguage) {
        this.inputText = inputText;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translatedText = translatedText;
        this.speechLanguage = speechLanguage;
    }

    /**
     * Swap target and source language
     */
    public void swapLanguages() {
        String target = targetLanguage;
        targetLanguage = sourceLanguage;
        sourceLanguage = target;
    }

    /**
     * Get the speech language
     * @return speechLanguage
     */
    public String getSpeechLanguage() {
        return speechLanguage;
    }

    /**
     * Set the
     * @param speechLanguage
     */
    public void setSpeechLanguage(String speechLanguage) {
        this.speechLanguage = speechLanguage;
    }

    /**
     * Get the input text
     * @return inputText
     */
    public String getInputText() {
        return inputText;
    }

    /**
     * Set the input text
     * @param inputText text to be set
     */
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    /**
     * Get the source language
     * @return sourceLanguage
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * Set the source langauge
     * @param sourceLanguage text to be set
     */
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    /**
     * Get the target language
     * @return targetLanguage
     */
    public String getTargetLanguage() {
        return targetLanguage;
    }

    /**
     * Set the target language
     * @param targetLanguage text to be set
     */
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    /**
     * Get the translated text
     * @return translatedText
     */
    public String getTranslatedText() {
        return translatedText;
    }

    /**
     * Set the translated text
     * @param translatedText text to be set
     */
    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}

