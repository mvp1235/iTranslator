package com.example.mvp.itranslator;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ConversationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private ArrayList<String> languages;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private final int REQ_CODE_SPEECH_INPUT1 = 100;
    private final int REQ_CODE_SPEECH_INPUT2 = 101;

    private TextToSpeech tts;

    //For first person
    private TextView textInput1;
    private ImageButton speakBtn1;
    private Spinner targetLanguage1;

    //For second person
    private TextView textInput2;
    private ImageButton speakBtn2;
    private Spinner targetLanguage2;

    private TextView translatedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        setUpLanguagesArray();

        tts = new TextToSpeech(this, this);

        textInput1 = findViewById(R.id.textSpeechInput1);
        speakBtn1 = findViewById(R.id.speakBtn);
        targetLanguage1 = findViewById(R.id.targetLanguageSpinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, languages);
        targetLanguage1.setAdapter(adapter);

        //Retrieve user's source language and set value to spinner on start
        //If language is supported, it will be set to that language. Otherwise, first value is chosen
        String targetLang1 = getDatabaseColumnValue(UserTable.SOURCE_LANG);
        targetLanguage1.setSelection(languages.indexOf(targetLang1));

        speakBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLanguage = targetLanguage1.getSelectedItem().toString();
                selectedLanguage = HomeActivity.languageInitials.get(selectedLanguage);
                promptSpeechInput1(selectedLanguage);
            }
        });

        textInput2 = findViewById(R.id.textSpeechInput2);
        speakBtn2 = findViewById(R.id.speakBtn2);

        //Retrieve user's target language and set value to spinner on start
        //If language is supported, it will be set to that language. Otherwise, first value is chosen
        String targetLang2 = getDatabaseColumnValue(UserTable.TARGET_LANG);
        targetLanguage2 = findViewById(R.id.targetLanguageSpinner2);
        targetLanguage2.setAdapter(adapter);
        targetLanguage2.setSelection(languages.indexOf(targetLang2));

        speakBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLanguage = targetLanguage2.getSelectedItem().toString();
                selectedLanguage = HomeActivity.languageInitials.get(selectedLanguage);
                promptSpeechInput2(selectedLanguage);
            }
        });

        translatedText = findViewById(R.id.translatedText);

    }

    /**
     * Return value of a certain column in the user table
     * @param columnName the column to be return
     * @return the value of the specified column name
     */
    public String getDatabaseColumnValue(String columnName) {
        Cursor c = getContentResolver().query(MyContentProvider.CONTENT_URI, null, "_id = ?", new String[] {"1"}, UserTable._ID);

        if (c.moveToFirst()) {
            do {
                if (columnName.equalsIgnoreCase(UserTable._ID)) {
                    String name = c.getString(c.getColumnIndex(UserTable._ID));
                    return name;
                } else if (columnName.equalsIgnoreCase(UserTable.NAME)) {
                    String gender = c.getString(c.getColumnIndex(UserTable.NAME));
                    return gender;
                } else if (columnName.equalsIgnoreCase(UserTable.SOURCE_LANG)) {
                    String source = c.getString(c.getColumnIndex(UserTable.SOURCE_LANG));
                    return source;
                } else if (columnName.equalsIgnoreCase(UserTable.TARGET_LANG)) {
                    String target = c.getString(c.getColumnIndex(UserTable.TARGET_LANG));
                    return target;
                } else if (columnName.equalsIgnoreCase(UserTable.SPEECH_LANG)) {
                    String speech = c.getString(c.getColumnIndex(UserTable.SPEECH_LANG));
                    return speech;
                } else if (columnName.equalsIgnoreCase(UserTable.SHAKE_TO_SPEAK)) {
                    int shakeToSpeak = c.getInt(c.getColumnIndex(UserTable.SHAKE_TO_SPEAK));
                    return Integer.toString(shakeToSpeak);
                } else if (columnName.equalsIgnoreCase(UserTable.LONG_PRESS_COPY)) {
                    int longPressCopy = c.getInt(c.getColumnIndex(UserTable.LONG_PRESS_COPY));
                    return Integer.toString(longPressCopy);
                }
            } while (c.moveToNext());
        }
        return null;
    }

    /**
     * Showing google speech input dialog for person 1
     * */
    private void promptSpeechInput1(String language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT1);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Showing google speech input dialog for person 2
     * */
    private void promptSpeechInput2(String language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT2);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
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

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT1: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textInput1.setText(result.get(0));
                    translate(result.get(0), 1);
                }
                break;
            }

            case REQ_CODE_SPEECH_INPUT2: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textInput2.setText(result.get(0));
                    translate(result.get(0), 2);
                }
                break;
            }

        }
    }

    private void translate(String sourceString, final int personNum) {
        //Update UI
        if (personNum == 1)
            translatedText.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
        else
            translatedText.setBackgroundColor(getResources().getColor(R.color.colorLightGreen));

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";

        String targetLanguage;
        if (personNum == 1)
            targetLanguage = targetLanguage2.getSelectedItem().toString();
        else
            targetLanguage = targetLanguage1.getSelectedItem().toString();

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
                                String result = translations.getJSONObject(0).getString("translatedText");
                                translatedText.setText(result);

                                String text = translatedText.getText().toString();
                                String language = "";
                                if (personNum == 1)
                                    language = targetLanguage2.getSelectedItem().toString();
                                else
                                    language = targetLanguage1.getSelectedItem().toString();

                                language = HomeActivity.languageInitials.get(language);

                                int languageAvailable = tts.setLanguage(new Locale(language));
                                if (languageAvailable == TextToSpeech.LANG_MISSING_DATA
                                        || languageAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    Log.e("TTS", "This Language is not supported");
                                } else {
                                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                                }

                            } else {
                                //Display the reason why the translation is failing
                                JSONObject error  = reader.getJSONObject("error");
                                String errorMessage = error.getString("message");
                                translatedText.setText(errorMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                translatedText.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

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

    /**
     * Google text-to-speech functionality only supports these (30) languages, unlike the broad support for google cloud translation
     */
    private void setUpLanguagesArray() {
        languages = new ArrayList<>();
        languages.add("Bengali");
        languages.add("Chinese (Simplified)");
        languages.add("Chinese (Traditional)");
        languages.add("Czech");
        languages.add("Danish");
        languages.add("Dutch");
        languages.add("English");
        languages.add("Finnish");
        languages.add("French");
        languages.add("German");
        languages.add("Greek");
        languages.add("Hindi");
        languages.add("Hungarian");
        languages.add("Indonesian");
        languages.add("Italian");
        languages.add("Japanese");
        languages.add("Khmer");
        languages.add("Korean");
        languages.add("Nepali");
        languages.add("Norwegian");
        languages.add("Polish");
        languages.add("Portuguese (Portugal, Brazil)");
        languages.add("Russian");
        languages.add("Sinhala (Sinhalese)");
        languages.add("Spanish");
        languages.add("Swedish");
        languages.add("Thai");
        languages.add("Turkish");
        languages.add("Ukrainian");
        languages.add("Vietnamese");
    }

}



































