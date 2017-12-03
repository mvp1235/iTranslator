package com.example.mvp.itranslator;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static com.example.mvp.itranslator.HomeActivity.languageInitialsReversed;
import static com.example.mvp.itranslator.HomeActivity.languages;

public class PhotoTranslateActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int REQUEST_IMAGE_CAPTURE = 9000;
    private static final int REQUEST_GALLERY_PHOTO = 9001;

    private TextToSpeech tts;

    private ImageView photoTV;
    private ImageButton getPhotoBtn;
    private Button translateBtn, speakBtn;
    private TextView resultTV, detectedLangTV, sourceTextTV, speakErrorTV;
    private Spinner targetLanguageSpinner;

    private AlertDialog photoActionDialog;
    private Bitmap photoBitmap = null;

    private ClipboardManager clipboardManager;
    private ClipData clipData;

    private PhotoTranslation photoTranslation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_translate);

        photoTranslation = new PhotoTranslation();

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        tts = new TextToSpeech(this, this);

        photoTV = findViewById(R.id.inputPhoto);
        getPhotoBtn = findViewById(R.id.getPhotoBtn);
        translateBtn = findViewById(R.id.photoTextTranslateBtn);
        resultTV = findViewById(R.id.photoResultTV);
        targetLanguageSpinner = findViewById(R.id.photoTargetLangSpinnner);
        detectedLangTV = findViewById(R.id.photoLanguageDetectedTV);
        sourceTextTV = findViewById(R.id.photoSourceText);
        speakErrorTV = findViewById(R.id.photoTranslateError);
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

        targetLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = targetLanguageSpinner.getItemAtPosition(position).toString();
                photoTranslation.setTargetLanguage(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String targetLang = getDatabaseColumnValue(UserTable.TARGET_LANG);
        targetLanguageSpinner.setSelection(languages.indexOf(targetLang));

        if (getDatabaseColumnValue(UserTable.LONG_PRESS_COPY).equalsIgnoreCase("1")) {
            resultTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copy(resultTV.getText().toString());
                    return false;
                }
            });

            sourceTextTV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copy(sourceTextTV.getText().toString());
                    return false;
                }
            });
        }

        //Setting listener for shaking motion sensor
        if (getDatabaseColumnValue(UserTable.LONG_PRESS_COPY).equalsIgnoreCase("1")) {
            ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
                @Override
                public void OnShake() {
                    if (getDatabaseColumnValue(UserTable.SHAKE_TO_SPEAK).equalsIgnoreCase("1"))
                        speakOutTranslatedText();
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    public void copy(String text) {
        clipData = ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(), "------Copied------\n" + text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Return value of a certain column in the user table
     * @param columnName the column to be return
     * @return the value of the specified column name
     */
    private String getDatabaseColumnValue(String columnName) {
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

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        ShakeDetector.destroy();
        super.onDestroy();
    }

    private void speakOutTranslatedText() {
        String text = photoTranslation.getTranslatedResult();
        String language = photoTranslation.getTargetLanguage();
        language = HomeActivity.languageInitials.get(language);
        int languageAvailable = tts.setLanguage(new Locale(language));
        if (languageAvailable == TextToSpeech.LANG_MISSING_DATA
                || languageAvailable == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "This Language is not supported");
            speakErrorTV.setText(targetLanguageSpinner.getSelectedItem().toString() + " is not supported for speech.");
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

                                photoTranslation.setSourceLanguage(languageInitialsReversed.get(language));
                                detectedLangTV.setText(languageInitialsReversed.get(language));

                                photoTranslation.setSourceText(capturedText);
                                sourceTextTV.setText(capturedText);
                                translate(capturedText);

                            } else {
                                //Display the reason why the translation is failing
                                resultTV.setText("Cannot detect the text from the photo. Please give another shot.");
                                photoTranslation.setTranslatedResult("");
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

        String targetLanguage = photoTranslation.getTargetLanguage();
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

                                photoTranslation.setTranslatedResult(translatedText);
                                resultTV.setText(translatedText);
                            } else {
                                //Display the reason why the translation is failing
                                JSONObject error  = reader.getJSONObject("error");
                                String errorMessage = error.getString("message");

                                photoTranslation.setTranslatedResult("");
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
