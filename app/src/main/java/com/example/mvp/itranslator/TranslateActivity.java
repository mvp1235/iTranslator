package com.example.mvp.itranslator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class TranslateActivity extends AppCompatActivity {

    private TextView resultTV;
    private EditText textInput;
    private Button translateBtn, clearBtn;
    private Spinner languageSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        resultTV = findViewById(R.id.resultTV);
        textInput = findViewById(R.id.textInput);
        translateBtn = findViewById(R.id.translateBtn);
        clearBtn = findViewById(R.id.clearBtn);
        languageSpinner = findViewById(R.id.targetLanguageSpinner);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInput.setText("");
            }
        });
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
}
