package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 * Model object for Photo Translation Activity, including a target language, source language, source text, and translated result
 */
public class PhotoTranslation {
    private String targetLanguage;
    private String sourceLanguage;
    private String sourceText;
    private String translatedResult;

    /**
     * Empty Constructor
     */
    public PhotoTranslation() {}

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
     * Get the source language
     * @return sourceLanguage
     */
    public String getSourceLanguage() {
        return sourceLanguage;
    }

    /**
     * Set the source language
     * @param sourceLanguage text to be set
     */
    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    /**
     * Get the source text
     * @return sourceText
     */
    public String getSourceText() {
        return sourceText;
    }

    /**
     * Set the source text
     * @param sourceText text to be set
     */
    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    /**
     * Get the translated result
     * @return translatedResult
     */
    public String getTranslatedResult() {
        return translatedResult;
    }

    /**
     * Set the translated result
     * @param translatedResult text to be set
     */
    public void setTranslatedResult(String translatedResult) {
        this.translatedResult = translatedResult;
    }
}
