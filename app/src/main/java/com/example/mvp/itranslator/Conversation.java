package com.example.mvp.itranslator;

/**
 * Created by MVP on 12/2/2017.
 * Model object for the Conversation Mode
 * Two inputs for each person speaking
 * Two langguages for each person speaking
 * A translated text will be set to the result of text translation
 */
public class Conversation {
    private String firstInput;
    private String secondInput;
    private String firstLanguage;
    private String secondLanguage;
    private String translatedText;

    /**
     * Empty initilaization of Conversation object
     */
    public Conversation() {}


    /**
     * Get the first input
     * @return firstInput
     */
    public String getFirstInput() {
        return firstInput;
    }

    /**
     * Set the first input
     * @param firstInput the string to be set
     */
    public void setFirstInput(String firstInput) {
        this.firstInput = firstInput;
    }

    /**
     * Get the second input
     * @return secondInput
     */
    public String getSecondInput() {
        return secondInput;
    }

    /**
     * Set the second input
     * @param secondInput the string to be set
     */
    public void setSecondInput(String secondInput) {
        this.secondInput = secondInput;
    }

    /**
     * Get the first language
     * @return firstLanguage
     */
    public String getFirstLanguage() {
        return firstLanguage;
    }

    /**
     * Set the first language
     * @param firstLanguage the language to be set
     */
    public void setFirstLanguage(String firstLanguage) {
        this.firstLanguage = firstLanguage;
    }

    /**
     * Get the second language
     * @return secondLanguage
     */
    public String getSecondLanguage() {
        return secondLanguage;
    }

    /**
     * Set the second language
     * @param secondLanguage the string to be set
     */
    public void setSecondLanguage(String secondLanguage) {
        this.secondLanguage = secondLanguage;
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
     * @param translatedText the string to be set
     */
    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
