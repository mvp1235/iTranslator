package com.example.mvp.itranslator;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

public class ConversationActivity extends AppCompatActivity {

    private final String CLOUD_API_KEY = "AIzaSyBtnT5Ln0j-t6q2ju98N7wM_LdbLd9yBxo";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private final int REQ_CODE_SPEECH_INPUT1 = 100;
    private final int REQ_CODE_SPEECH_INPUT2 = 101;

    //For first person
    private TextView textInput1;
    private Button speakBtn1;
    private Spinner targetLanguage1;

    //For second person
    private TextView textInput2;
    private Button speakBtn2;
    private Spinner targetLanguage2;

    private TextView translatedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        textInput1 = findViewById(R.id.textSpeechInput);
        speakBtn1 = findViewById(R.id.speakBtn);
        targetLanguage1 = findViewById(R.id.targetLanguageSpinner);

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
        targetLanguage2 = findViewById(R.id.targetLanguageSpinner2);

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

    private void translate() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";
        String sourceString = textInput.getText().toString();
        String apiKey = "AIzaSyBtnT5Ln0j-t6q2ju98N7wM_LdbLd9yBxo";

        String targetLanguage = languageSpinner.getSelectedItem().toString();
        targetLanguage = HomeActivity.languageInitials.get(targetLanguage);

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
                }
                break;
            }

            case REQ_CODE_SPEECH_INPUT2: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textInput2.setText(result.get(0));
                }
                break;
            }

        }
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

}



































