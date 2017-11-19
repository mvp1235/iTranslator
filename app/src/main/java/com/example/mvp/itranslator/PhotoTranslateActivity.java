package com.example.mvp.itranslator;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class PhotoTranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_IMAGE_CAPTURE = 9000;
    private static final int REQUEST_GALLERY_PHOTO = 9001;
    private static final int REQ_CODE_SPEECH_INPUT = 9002;

    private ArrayList<String> languages;
    private TextToSpeech tts;

    private ImageView photoTV;
    private ImageButton getPhotoBtn;
    private Button translateBtn, speakBtn;
    private TextView resultTV, detectedLangTV, sourceTextTV;
    private Spinner targetLanguageSpinner;

    private AlertDialog photoActionDialog;
    private Bitmap photoBitmap = null;

    private HashMap<String, String> languageInitials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_translate);

        setUpLanguagesArray();
        setUpLanguageInitials();

        tts = new TextToSpeech(this, this);

        photoTV = findViewById(R.id.inputPhoto);
        getPhotoBtn = findViewById(R.id.getPhotoBtn);
        translateBtn = findViewById(R.id.photoTextTranslateBtn);
        resultTV = findViewById(R.id.photoResultTV);
        targetLanguageSpinner = findViewById(R.id.photoTargetLangSpinnner);
        detectedLangTV = findViewById(R.id.photoLanguageDetectedTV);
        sourceTextTV = findViewById(R.id.photoSourceText);
        speakBtn = findViewById(R.id.photoSpeakBtn);

        getPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoActionDialog();
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateTextFromPhoto();
            }
        });

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutTranslatedText();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, languages);
        targetLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setSelection(languages.indexOf("English"));

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
        String language = targetLanguageSpinner.getSelectedItem().toString();
        language = HomeActivity.languageInitials.get(language);
        int languageAvailable = tts.setLanguage(new Locale(language));
        if (languageAvailable == TextToSpeech.LANG_MISSING_DATA
                || languageAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "This Language is not supported");
            Toast.makeText(getApplicationContext(), "This language is not supported.", Toast.LENGTH_SHORT);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photoBitmap = (Bitmap) extras.get("data");
            photoTV.setImageBitmap(photoBitmap);
            photoActionDialog.dismiss();

        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoBitmap != null) {
                photoTV.setImageBitmap(photoBitmap);
                photoActionDialog.dismiss();
            }
        }
    }

    private void translateTextFromPhoto() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject requestJSON = null;
        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + HomeActivity.CLOUD_API_KEY;

        try {
            requestJSON = new JSONObject("{\n" +
                    "  \"requests\": [\n" +
                    "    {\n" +
                    "      \"image\": {\n" +
                    "        \"content\": \"" +  encodeBitmap(photoBitmap) + "\"\n" +
                    "      },\n" +
                    "      \"features\": [\n" +
                    "        {\n" +
                    "          \"type\": \"TEXT_DETECTION\"\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a string response from the provided URL.
        JsonObjectRequest  jsonObjectRequest = new JsonObjectRequest (url, requestJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data  = response.getJSONArray("responses").getJSONObject(0);

                            if (data.has("textAnnotations")) {
                                JSONArray textAnnotations = data.getJSONArray("textAnnotations");
                                JSONObject result = textAnnotations.getJSONObject(0);
                                String capturedText = result.getString("description");
                                String language = result.getString("locale");
                                detectedLangTV.setText(languageInitials.get(language));
                                sourceTextTV.setText(capturedText);
                                translate(capturedText);

                            } else {
                                //Display the reason why the translation is failing
                                resultTV.setText("Cannot detect the text from the photo. Please give another shot.");
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

        queue.add(jsonObjectRequest);
    }

    private void translate(String sourceString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";
//        String sourceString = resultTV.getText().toString();

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

    private void showPhotoActionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(PhotoTranslateActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_pick_photos, null);
        LinearLayout galleryLL = mView.findViewById(R.id.galleryLL);
        LinearLayout cameraLL = mView.findViewById(R.id.cameraLL);

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY_PHOTO);
            }
        });

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mBuilder.setView(mView);
        photoActionDialog = mBuilder.create();
        photoActionDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private String encodeBitmap(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        return imageEncoded;
    }

    private void setUpLanguageInitials() {
        languageInitials = new HashMap<>();
        languageInitials.put("af", "Afrikaans");
        languageInitials.put("sq", "Albanian");
        languageInitials.put("am", "Amharic");
        languageInitials.put("ar", "Arabic");
        languageInitials.put("hy", "Armenian");
        languageInitials.put("az", "Azeerbaijani");
        languageInitials.put("eu", "Basque");
        languageInitials.put("be", "Belarusian");
        languageInitials.put("bn", "Bengali");
        languageInitials.put("bs", "Bosnian");
        languageInitials.put("bg", "Bulgarian");
        languageInitials.put("ca", "Catalan");
        languageInitials.put("ceb", "Cebuano");
        languageInitials.put("zh-CN", "Chinese (Simplified)");
        languageInitials.put("zh-TW", "Chinese (Traditional)");
        languageInitials.put("co", "Corsican");
        languageInitials.put("hr", "Croatian");
        languageInitials.put("cs", "Czech");
        languageInitials.put("da", "Danish");
        languageInitials.put("nl", "Dutch");
        languageInitials.put("en", "English");
        languageInitials.put("eo", "Esperanto");
        languageInitials.put("et", "Estonian");
        languageInitials.put("fi", "Finnish");
        languageInitials.put("fr", "French");
        languageInitials.put("fy", "Frisian");
        languageInitials.put("gl", "Galician");
        languageInitials.put("ka", "Georgian");
        languageInitials.put("de", "German");
        languageInitials.put("el", "Greek");
        languageInitials.put("gu", "Gujarati");
        languageInitials.put("ht", "Haitian Creole");
        languageInitials.put("ha", "Hausa");
        languageInitials.put("haw", "Hawaiian");
        languageInitials.put("iw", "Hebrew");
        languageInitials.put("hi", "Hindi");
        languageInitials.put("hmm", "Hmong");
        languageInitials.put("hu", "Hungarian");
        languageInitials.put("is", "Icelandic");
        languageInitials.put("ig", "Igbo");
        languageInitials.put("id", "Indonesian");
        languageInitials.put("ga", "Irish");
        languageInitials.put("it", "Italian");
        languageInitials.put("ja", "Japanese");
        languageInitials.put("jw", "Javanese");
        languageInitials.put("kn", "Kannada");
        languageInitials.put("kk", "Kazakh");
        languageInitials.put("km", "Khmer");
        languageInitials.put("ko", "Korean");
        languageInitials.put("ku", "Kurdish");
        languageInitials.put("ky", "Kyrgyz");
        languageInitials.put("lo", "Lao");
        languageInitials.put("la", "Latin");
        languageInitials.put("lv", "Latvian");
        languageInitials.put("lt", "Lithuanian");
        languageInitials.put("af", "Afrikaans");
        languageInitials.put("lb", "Luxembourgish");
        languageInitials.put("mk", "Macedonian");
        languageInitials.put("mg", "Malagasy");
        languageInitials.put("ms", "Malay");
        languageInitials.put("ml", "Malayalam");
        languageInitials.put("mt", "Maltese");
        languageInitials.put("mi", "Maori");
        languageInitials.put("mr", "Marathi");
        languageInitials.put("mn", "Mongolian");
        languageInitials.put("my", "Myanmar (Burmese)");
        languageInitials.put("ne", "Nepali");
        languageInitials.put("no", "Norwegian");
        languageInitials.put("ny", "Nyanja (Chichewa)");
        languageInitials.put("ps", "Pashto");
        languageInitials.put("fa", "Persian");
        languageInitials.put("pl", "Polish");
        languageInitials.put("pt", "Portuguese (Portugal, Brazil)");
        languageInitials.put("pa", "Punjabi");
        languageInitials.put("ro", "Romanian");
        languageInitials.put("ru", "Russian");
        languageInitials.put("sm", "Samoan");
        languageInitials.put("gd", "Scots Gaelic");
        languageInitials.put("sr", "Serbian");
        languageInitials.put("st", "Sesotho");
        languageInitials.put("sn", "Shona");
        languageInitials.put("sd", "Sindhi");
        languageInitials.put("si", "Sinhala (Sinhalese)");
        languageInitials.put("sk", "Slovak");
        languageInitials.put("sl", "Slovenian");
        languageInitials.put("so", "Somali");
        languageInitials.put("es", "Spanish");
        languageInitials.put("su", "Sundanese");
        languageInitials.put("sw", "Swahili");
        languageInitials.put("sv", "Swedish");
        languageInitials.put("tl", "Tagalog (Filipino)");
        languageInitials.put("tg", "Tajik");
        languageInitials.put("ta", "Tamil");
        languageInitials.put("te", "Telugu");
        languageInitials.put("th", "Thai");
        languageInitials.put("ur", "Turkish");
        languageInitials.put("uk", "Ukrainian");
        languageInitials.put("ur", "Urdu");
        languageInitials.put("uz", "Uzbek");
        languageInitials.put("vi", "Vietnamese");
        languageInitials.put("cy", "Welsh");
        languageInitials.put("xh", "Xhosa");
        languageInitials.put("yi", "Yiddish");
        languageInitials.put("yo", "Yoruba");
        languageInitials.put("zu", "Zulu");
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
