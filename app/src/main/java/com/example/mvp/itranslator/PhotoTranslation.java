package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 */

public class PhotoTranslation {
    private String targetLanguage;
    private String sourceLanguage;
    private String sourceText;
    private String translatedResult;

    public PhotoTranslation() {}

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTranslatedResult() {
        return translatedResult;
    }

    public void setTranslatedResult(String translatedResult) {
        this.translatedResult = translatedResult;
    }
}
