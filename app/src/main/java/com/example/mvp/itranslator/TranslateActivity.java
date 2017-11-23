package com.example.mvp.itranslator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Locale;


public class TranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private static final int REQ_CODE_SPEECH_INPUT = 10000;
    private ArrayList<String> languages;
    private TextToSpeech tts;

    private TextView resultTV, errorTV;
    private EditText textInput;
    private Button translateBtn, clearBtn;
    private ImageButton speakTranslationBtn;
    private ImageButton speakBtn;
    private Spinner speechLanguageSpinner;

    private Spinner sourceLanguageSpinner, targetLanguageSpinner;
    private ImageButton swapBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        setUpLanguagesArray();

        tts = new TextToSpeech(this, this);

        resultTV = findViewById(R.id.resultTV);
        errorTV = findViewById(R.id.translateSpeakErrorTV);
        textInput = findViewById(R.id.textInput);
        translateBtn = findViewById(R.id.translateBtn);
        clearBtn = findViewById(R.id.clearBtn);
        speakBtn = findViewById(R.id.translateSpeakBtn);
        speakTranslationBtn = findViewById(R.id.translatedSpeakBtn);

        swapBtn = findViewById(R.id.translateSwapBtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);


        speechLanguageSpinner = findViewById(R.id.translateSpeechLangSpinner);
        speechLanguageSpinner.setAdapter(adapter);
        // Set default speech language to English
        speechLanguageSpinner.setSelection(languages.indexOf("English"));

        sourceLanguageSpinner = findViewById(R.id.translateSourceLanguageSpinner);
        targetLanguageSpinner = findViewById(R.id.translateTargetLanguageSpinner);

        sourceLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setAdapter(adapter);

        //Setting default languages for spinners
        sourceLanguageSpinner.setSelection(languages.indexOf("English"));
        targetLanguageSpinner.setSelection(languages.indexOf("Vietnamese"));

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputs();
            }
        });
        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLanguage = speechLanguageSpinner.getSelectedItem().toString();
                selectedLanguage = HomeActivity.languageInitials.get(selectedLanguage);
                promptSpeechInput(selectedLanguage);
            }
        });

        speakTranslationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutTranslatedText();
            }
        });

        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //swap values between the source and target language spinner
                swapLanguageSpinnerValues();
            }
        });

    }

    private void swapLanguageSpinnerValues() {
        int source = sourceLanguageSpinner.getSelectedItemPosition();
        int target = targetLanguageSpinner.getSelectedItemPosition();
        sourceLanguageSpinner.setSelection(target);
        targetLanguageSpinner.setSelection(source);
        clearInputs();
    }

    private void clearInputs() {
        textInput.setText("");
        resultTV.setText("");
        errorTV.setText("");
    }

    /**
     * Showing google speech input dialog for person 1
     * */
    private void promptSpeechInput(String language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textInput.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakOutTranslatedText() {
        String text = resultTV.getText().toString();
        if (text.isEmpty())
            return;
        
        String language = targetLanguageSpinner.getSelectedItem().toString();
        language = HomeActivity.languageInitials.get(language);
        int languageAvailable = tts.setLanguage(new Locale(language));
        if (languageAvailable == TextToSpeech.LANG_MISSING_DATA
                || languageAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "This Language is not supported");
            errorTV.setText("This language is not supported for speech.");
        } else {
            errorTV.setText("");
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void translate() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";
        String sourceString = textInput.getText().toString();

        String targetLanguage = targetLanguageSpinner.getSelectedItem().toString();
        targetLanguage = HomeActivity.languageInitials.get(targetLanguage);

        try {
            sourceString = URLEncoder.encode(sourceString,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url += "?q=" + sourceString + "&key=" + HomeActivity.CLOUD_API_KEY + "&target=" + targetLanguage;

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

    /**
     * Called to signal the completion of the TextToSpeech engine initialization.
     *
     * @param status {@link TextToSpeech#SUCCESS} or {@link TextToSpeech#ERROR}.
     */
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);    //default langauge is English

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // do stuff

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }
}
