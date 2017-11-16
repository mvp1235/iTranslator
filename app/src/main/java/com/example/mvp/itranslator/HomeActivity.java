package com.example.mvp.itranslator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    public static HashMap<String, String> languageInitials;

    private ImageButton translateBtn, conversationBtn, placesBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpLanguageInitials();

        //Referencing to the UI elements for usage
        translateBtn = findViewById(R.id.translateBtn);
        conversationBtn = findViewById(R.id.conversationBtn);
        placesBtn = findViewById(R.id.placesBtn);
        profileBtn = findViewById(R.id.profileBtn);

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
                enterConversatinMode();
            }
        });

        placesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPlacesMode();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
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
    private void enterConversatinMode() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    /**
     * Takes users to places mdoe, where they can look for places to visit nearby
     */
    private void enterPlacesMode() {

    }

    /**
     * Take users to profile mode, where they can view/edit their personal information
     */
    private void enterProfileMode() {

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

}
