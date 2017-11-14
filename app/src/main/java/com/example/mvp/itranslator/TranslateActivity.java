package com.example.mvp.itranslator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;


public class TranslateActivity extends AppCompatActivity {

    private TextView resultTV;
    private EditText textInput;
    private Button translateBtn;
    private Spinner languageSpinner;
    private HashMap<String, String> languageInitials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        setUpLanguageInitials();

        resultTV = findViewById(R.id.resultTV);
        textInput = findViewById(R.id.textInput);
        translateBtn = findViewById(R.id.translateBtn);
        languageSpinner = findViewById(R.id.targetLanguageSpinner);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });
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

    private void translate() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";
        String sourceString = textInput.getText().toString();
        String apiKey = "AIzaSyBtnT5Ln0j-t6q2ju98N7wM_LdbLd9yBxo";

        String targetLanguage = languageSpinner.getSelectedItem().toString();
        targetLanguage = languageInitials.get(targetLanguage);

        try {
            sourceString = URLEncoder.encode(sourceString,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url += "?q=" + sourceString + "&key=" + apiKey + "&target=" + targetLanguage;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject reader = new JSONObject(response);
                            if (reader.getJSONObject("data") != null) {
                                JSONObject data  = reader.getJSONObject("data");
                                JSONArray translations = data.getJSONArray("translations");
                                String translatedText = translations.getJSONObject(0).getString("translatedText");
                                resultTV.setText(translatedText);
                            } else {
                                //Display the reason why the translation is failing
                                JSONObject error  = reader.getJSONObject("error");
                                String errorMessage = error.getString("message");
                                resultTV.setText(errorMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultTV.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
