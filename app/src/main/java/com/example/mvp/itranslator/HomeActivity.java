package com.example.mvp.itranslator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    //This key will not be available on app launched. User will need to provide his/her own API key to use the app
    //The app will show users instructions on how to obtain this API key
    public static final String CLOUD_API_KEY = "AIzaSyBtnT5Ln0j-t6q2ju98N7wM_LdbLd9yBxo";

    public static final String MY_PREFERENCE = "MyPrefs";

    public static HashMap<String, String> languageInitials;
    public static HashMap<String, String> languageInitialsReversed;
    public static ArrayList<String> languages;

    private ImageButton translateBtn, conversationBtn, photoBtn, placesBtn;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpLanguagesArray();
        setUpLanguageInitials();
        setUpLanguageInitialsReversed();

        sharedPref = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //If running the app for the very first time, a default user profile will be created
        if (sharedPref.getBoolean("FIRST_RUN", true)) {
            populateDefaultDatabase();
            editor.putBoolean("FIRST_RUN", false);
            editor.commit();
        }


        //Referencing to the UI elements for usage
        translateBtn = findViewById(R.id.translateBtn);
        conversationBtn = findViewById(R.id.conversationBtn);
        photoBtn = findViewById(R.id.photosBtn);
        placesBtn = findViewById(R.id.placesBtn);

        setUpClickListeners();
    }


    /**
     * Set up listeners for four buttons mode on home screen
     */
    public void setUpClickListeners() {
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterTranslateMode();
            }
        });

        conversationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterConversationMode();
            }
        });

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPhotoTranslationMode();
            }
        });

        placesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProfileMode();
            }
        });
    }

    /**
     * Take users to translate mode, where they can input text by manually typing or speaking, and the app will translate the input into requested language
     */
    private void enterTranslateMode() {
        Intent intent = new Intent(this, TranslateActivity.class);
        startActivity(intent);
    }

    /**
     * Take users to conversation mode, where the app will await for the users' voice input, automatically translate it to requested language, and speak it out
     */
    private void enterConversationMode() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    /**
     * Takes users to places mdoe, where they can look for places to visit nearby
     */
    private void enterPhotoTranslationMode() {
        Intent intent = new Intent(this, PhotoTranslateActivity.class);
        startActivity(intent);
    }

    /**
     * Take users to profile mode, where they can view/edit their personal information
     */
    private void enterProfileMode() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void setUpLanguagesArray() {
        languages = new ArrayList<>();
        languages.add("Afrikaans");
        languages.add("Albanian");
        languages.add("Amharic");
        languages.add("Arabic");
        languages.add("Armenian");
        languages.add("Azeerbaijani");
        languages.add("Basque");
        languages.add("Belarusian");
        languages.add("Bengali");
        languages.add("Bosnian");
        languages.add("Bulgarian");
        languages.add("Catalan");
        languages.add("Cebuano");
        languages.add("Chinese (Simplified)");
        languages.add("Chinese (Traditional)");
        languages.add("Corsican");
        languages.add("Croatian");
        languages.add("Czech");
        languages.add("Danish");
        languages.add("Dutch");
        languages.add("English");
        languages.add("Esperanto");
        languages.add("Estonian");
        languages.add("Finnish");
        languages.add("French");
        languages.add("Frisian");
        languages.add("Galician");
        languages.add("Georgian");
        languages.add("German");
        languages.add("Greek");
        languages.add("Gujarati");
        languages.add("Haitian Creole");
        languages.add("Hausa");
        languages.add("Hawaiian");
        languages.add("Hebrew");
        languages.add("Hindi");
        languages.add("Hmong");
        languages.add("Hungarian");
        languages.add("Icelandic");
        languages.add("Igbo");
        languages.add("Indonesian");
        languages.add("Irish");
        languages.add("Italian");
        languages.add("Japanese");
        languages.add("Javanese");
        languages.add("Kannada");
        languages.add("Kazakh");
        languages.add("Khmer");
        languages.add("Korean");
        languages.add("Kurdish");
        languages.add("Kyrgyz");
        languages.add("Lao");
        languages.add("Latin");
        languages.add("Latvian");
        languages.add("Lithuanian");
        languages.add("Luxembourgish");
        languages.add("Macedonian");
        languages.add("Malagasy");
        languages.add("Malay");
        languages.add("Malayalam");
        languages.add("Maltese");
        languages.add("Maori");
        languages.add("Marathi");
        languages.add("Mongolian");
        languages.add("Myanmar (Burmese)");
        languages.add("Nepali");
        languages.add("Norwegian");
        languages.add("Nyanja (Chichewa)");
        languages.add("Pashto");
        languages.add("Persian");
        languages.add("Polish");
        languages.add("Portuguese (Portugal, Brazil)");
        languages.add("Punjabi");
        languages.add("Romanian");
        languages.add("Russian");
        languages.add("Samoan");
        languages.add("Scots Gaelic");
        languages.add("Serbian");
        languages.add("Sesotho");
        languages.add("Shona");
        languages.add("Sindhi");
        languages.add("Sinhala (Sinhalese)");
        languages.add("Slovak");
        languages.add("Slovenian");
        languages.add("Somali");
        languages.add("Spanish");
        languages.add("Sundanese");
        languages.add("Swahili");
        languages.add("Swedish");
        languages.add("Tagalog (Filipino)");
        languages.add("Tajik");
        languages.add("Tamil");
        languages.add("Telugu");
        languages.add("Thai");
        languages.add("Turkish");
        languages.add("Ukrainian");
        languages.add("Urdu");
        languages.add("Uzbek");
        languages.add("Vietnamese");
        languages.add("Welsh");
        languages.add("Xhosa");
        languages.add("Yiddish");
        languages.add("Yoruba");
        languages.add("Zulu");
    }

    private void setUpLanguageInitials() {
        languageInitials = new HashMap<>();
        languageInitials.put("Afrikaans", "af");
        languageInitials.put("Albanian", "sq");
        languageInitials.put("Amharic", "am");
        languageInitials.put("Arabic", "ar");
        languageInitials.put("Armenian", "hy");
        languageInitials.put("Azeerbaijani", "az");
        languageInitials.put("Basque", "eu");
        languageInitials.put("Belarusian", "be");
        languageInitials.put("Bengali", "bn");
        languageInitials.put("Bosnian", "bs");
        languageInitials.put("Bulgarian", "bg");
        languageInitials.put("Catalan", "ca");
        languageInitials.put("Cebuano", "ceb");
        languageInitials.put("Chinese (Simplified)", "zh-CN");
        languageInitials.put("Chinese (Traditional)", "zh-TW");
        languageInitials.put("Corsican", "co");
        languageInitials.put("Croatian", "hr");
        languageInitials.put("Czech", "cs");
        languageInitials.put("Danish", "da");
        languageInitials.put("Dutch", "nl");
        languageInitials.put("English", "en");
        languageInitials.put("Esperanto", "eo");
        languageInitials.put("Estonian", "et");
        languageInitials.put("Finnish", "fi");
        languageInitials.put("French", "fr");
        languageInitials.put("Frisian", "fy");
        languageInitials.put("Galician", "gl");
        languageInitials.put("Georgian", "ka");
        languageInitials.put("German", "de");
        languageInitials.put("Greek", "el");
        languageInitials.put("Gujarati", "gu");
        languageInitials.put("Haitian Creole", "ht");
        languageInitials.put("Hausa", "ha");
        languageInitials.put("Hawaiian", "haw");
        languageInitials.put("Hebrew", "iw");
        languageInitials.put("Hindi", "hi");
        languageInitials.put("Hmong", "hmm");
        languageInitials.put("Hungarian", "hu");
        languageInitials.put("Icelandic", "is");
        languageInitials.put("Igbo", "ig");
        languageInitials.put("Indonesian", "id");
        languageInitials.put("Irish", "ga");
        languageInitials.put("Italian", "it");
        languageInitials.put("Japanese", "ja");
        languageInitials.put("Javanese", "jw");
        languageInitials.put("Kannada", "kn");
        languageInitials.put("Kazakh", "kk");
        languageInitials.put("Khmer", "km");
        languageInitials.put("Korean", "ko");
        languageInitials.put("Kurdish", "ku");
        languageInitials.put("Kyrgyz", "ky");
        languageInitials.put("Lao", "lo");
        languageInitials.put("Latin", "la");
        languageInitials.put("Latvian", "lv");
        languageInitials.put("Lithuanian", "lt");
        languageInitials.put("Afrikaans", "af");
        languageInitials.put("Luxembourgish", "lb");
        languageInitials.put("Macedonian", "mk");
        languageInitials.put("Malagasy", "mg");
        languageInitials.put("Malay", "ms");
        languageInitials.put("Malayalam", "ml");
        languageInitials.put("Maltese", "mt");
        languageInitials.put("Maori", "mi");
        languageInitials.put("Marathi", "mr");
        languageInitials.put("Mongolian", "mn");
        languageInitials.put("Myanmar (Burmese)", "my");
        languageInitials.put("Nepali", "ne");
        languageInitials.put("Norwegian", "no");
        languageInitials.put("Nyanja (Chichewa)", "ny");
        languageInitials.put("Pashto", "ps");
        languageInitials.put("Persian", "fa");
        languageInitials.put("Polish", "pl");
        languageInitials.put("Portuguese (Portugal, Brazil)", "pt");
        languageInitials.put("Punjabi", "pa");
        languageInitials.put("Romanian", "ro");
        languageInitials.put("Russian", "ru");
        languageInitials.put("Samoan", "sm");
        languageInitials.put("Scots Gaelic", "gd");
        languageInitials.put("Serbian", "sr");
        languageInitials.put("Sesotho", "st");
        languageInitials.put("Shona", "sn");
        languageInitials.put("Sindhi", "sd");
        languageInitials.put("Sinhala (Sinhalese)", "si");
        languageInitials.put("Slovak", "sk");
        languageInitials.put("Slovenian", "sl");
        languageInitials.put("Somali", "so");
        languageInitials.put("Spanish", "es");
        languageInitials.put("Sundanese", "su");
        languageInitials.put("Swahili", "sw");
        languageInitials.put("Swedish", "sv");
        languageInitials.put("Tagalog (Filipino)", "tl");
        languageInitials.put("Tajik", "tg");
        languageInitials.put("Tamil", "ta");
        languageInitials.put("Telugu", "te");
        languageInitials.put("Thai", "th");
        languageInitials.put("Turkish", "ur");
        languageInitials.put("Ukrainian", "uk");
        languageInitials.put("Urdu", "ur");
        languageInitials.put("Uzbek", "uz");
        languageInitials.put("Vietnamese", "vi");
        languageInitials.put("Welsh", "cy");
        languageInitials.put("Xhosa", "xh");
        languageInitials.put("Yiddish", "yi");
        languageInitials.put("Yoruba", "yo");
        languageInitials.put("Zulu", "zu");
    }

    private void setUpLanguageInitialsReversed() {
        languageInitialsReversed = new HashMap<>();
        languageInitialsReversed.put("af", "Afrikaans");
        languageInitialsReversed.put("sq", "Albanian");
        languageInitialsReversed.put("am", "Amharic");
        languageInitialsReversed.put("ar", "Arabic");
        languageInitialsReversed.put("hy", "Armenian");
        languageInitialsReversed.put("az", "Azeerbaijani");
        languageInitialsReversed.put("eu", "Basque");
        languageInitialsReversed.put("be", "Belarusian");
        languageInitialsReversed.put("bn", "Bengali");
        languageInitialsReversed.put("bs", "Bosnian");
        languageInitialsReversed.put("bg", "Bulgarian");
        languageInitialsReversed.put("ca", "Catalan");
        languageInitialsReversed.put("ceb", "Cebuano");
        languageInitialsReversed.put("zh-CN", "Chinese (Simplified)");
        languageInitialsReversed.put("zh-TW", "Chinese (Traditional)");
        languageInitialsReversed.put("co", "Corsican");
        languageInitialsReversed.put("hr", "Croatian");
        languageInitialsReversed.put("cs", "Czech");
        languageInitialsReversed.put("da", "Danish");
        languageInitialsReversed.put("nl", "Dutch");
        languageInitialsReversed.put("en", "English");
        languageInitialsReversed.put("eo", "Esperanto");
        languageInitialsReversed.put("et", "Estonian");
        languageInitialsReversed.put("fi", "Finnish");
        languageInitialsReversed.put("fr", "French");
        languageInitialsReversed.put("fy", "Frisian");
        languageInitialsReversed.put("gl", "Galician");
        languageInitialsReversed.put("ka", "Georgian");
        languageInitialsReversed.put("de", "German");
        languageInitialsReversed.put("el", "Greek");
        languageInitialsReversed.put("gu", "Gujarati");
        languageInitialsReversed.put("ht", "Haitian Creole");
        languageInitialsReversed.put("ha", "Hausa");
        languageInitialsReversed.put("haw", "Hawaiian");
        languageInitialsReversed.put("iw", "Hebrew");
        languageInitialsReversed.put("hi", "Hindi");
        languageInitialsReversed.put("hmm", "Hmong");
        languageInitialsReversed.put("hu", "Hungarian");
        languageInitialsReversed.put("is", "Icelandic");
        languageInitialsReversed.put("ig", "Igbo");
        languageInitialsReversed.put("id", "Indonesian");
        languageInitialsReversed.put("ga", "Irish");
        languageInitialsReversed.put("it", "Italian");
        languageInitialsReversed.put("ja", "Japanese");
        languageInitialsReversed.put("jw", "Javanese");
        languageInitialsReversed.put("kn", "Kannada");
        languageInitialsReversed.put("kk", "Kazakh");
        languageInitialsReversed.put("km", "Khmer");
        languageInitialsReversed.put("ko", "Korean");
        languageInitialsReversed.put("ku", "Kurdish");
        languageInitialsReversed.put("ky", "Kyrgyz");
        languageInitialsReversed.put("lo", "Lao");
        languageInitialsReversed.put("la", "Latin");
        languageInitialsReversed.put("lv", "Latvian");
        languageInitialsReversed.put("lt", "Lithuanian");
        languageInitialsReversed.put("af", "Afrikaans");
        languageInitialsReversed.put("lb", "Luxembourgish");
        languageInitialsReversed.put("mk", "Macedonian");
        languageInitialsReversed.put("mg", "Malagasy");
        languageInitialsReversed.put("ms", "Malay");
        languageInitialsReversed.put("ml", "Malayalam");
        languageInitialsReversed.put("mt", "Maltese");
        languageInitialsReversed.put("mi", "Maori");
        languageInitialsReversed.put("mr", "Marathi");
        languageInitialsReversed.put("mn", "Mongolian");
        languageInitialsReversed.put("my", "Myanmar (Burmese)");
        languageInitialsReversed.put("ne", "Nepali");
        languageInitialsReversed.put("no", "Norwegian");
        languageInitialsReversed.put("ny", "Nyanja (Chichewa)");
        languageInitialsReversed.put("ps", "Pashto");
        languageInitialsReversed.put("fa", "Persian");
        languageInitialsReversed.put("pl", "Polish");
        languageInitialsReversed.put("pt", "Portuguese (Portugal, Brazil)");
        languageInitialsReversed.put("pa", "Punjabi");
        languageInitialsReversed.put("ro", "Romanian");
        languageInitialsReversed.put("ru", "Russian");
        languageInitialsReversed.put("sm", "Samoan");
        languageInitialsReversed.put("gd", "Scots Gaelic");
        languageInitialsReversed.put("sr", "Serbian");
        languageInitialsReversed.put("st", "Sesotho");
        languageInitialsReversed.put("sn", "Shona");
        languageInitialsReversed.put("sd", "Sindhi");
        languageInitialsReversed.put("si", "Sinhala (Sinhalese)");
        languageInitialsReversed.put("sk", "Slovak");
        languageInitialsReversed.put("sl", "Slovenian");
        languageInitialsReversed.put("so", "Somali");
        languageInitialsReversed.put("es", "Spanish");
        languageInitialsReversed.put("su", "Sundanese");
        languageInitialsReversed.put("sw", "Swahili");
        languageInitialsReversed.put("sv", "Swedish");
        languageInitialsReversed.put("tl", "Tagalog (Filipino)");
        languageInitialsReversed.put("tg", "Tajik");
        languageInitialsReversed.put("ta", "Tamil");
        languageInitialsReversed.put("te", "Telugu");
        languageInitialsReversed.put("th", "Thai");
        languageInitialsReversed.put("ur", "Turkish");
        languageInitialsReversed.put("uk", "Ukrainian");
        languageInitialsReversed.put("ur", "Urdu");
        languageInitialsReversed.put("uz", "Uzbek");
        languageInitialsReversed.put("vi", "Vietnamese");
        languageInitialsReversed.put("cy", "Welsh");
        languageInitialsReversed.put("xh", "Xhosa");
        languageInitialsReversed.put("yi", "Yiddish");
        languageInitialsReversed.put("yo", "Yoruba");
        languageInitialsReversed.put("zu", "Zulu");
    }

    /**
     * Insert a new user database, which initialize name to John Doe, and default source language to English, default target to Vietnamese, and default speech language to English
     */
    public void populateDefaultDatabase() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserTable.NAME, "John Doe");
        contentValues.put(UserTable.TARGET_LANG, "Vietnamese");
        contentValues.put(UserTable.SOURCE_LANG, "English");
        contentValues.put(UserTable.SPEECH_LANG, "English");
        contentValues.put(UserTable.LONG_PRESS_COPY, 0);
        contentValues.put(UserTable.SHAKE_TO_SPEAK, 0);
        getContentResolver().insert(MyContentProvider.CONTENT_URI, contentValues);
    }

}
