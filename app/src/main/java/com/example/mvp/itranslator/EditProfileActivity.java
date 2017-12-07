package com.example.mvp.itranslator;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import static com.example.mvp.itranslator.HomeActivity.languages;

public class EditProfileActivity extends AppCompatActivity {

    private EditText userNameET;
    private Spinner userSourceLanguageSpinner, userTargetLanguageSpinner, userSpeechLanguageSpinner;
    private Button saveBtn;
    private CheckBox shakeToSpeakCheckBox, longPressCopyCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userNameET = findViewById(R.id.editUserName);
        saveBtn = findViewById(R.id.saveProfileEditBtn);
        userSourceLanguageSpinner = findViewById(R.id.sourceLanguageSpinner);
        userTargetLanguageSpinner = findViewById(R.id.targetLanguageSpinner);
        userSpeechLanguageSpinner = findViewById(R.id.speechLanguageSpinner);
        shakeToSpeakCheckBox = findViewById(R.id.shakeToSpeakCheckBox);
        longPressCopyCheckBox = findViewById(R.id.longPressCopyCheckBox);

        Intent receivedIntent = getIntent();

        String name = receivedIntent.getStringExtra(ProfileActivity.NAME);
        String source = receivedIntent.getStringExtra(ProfileActivity.SOURCE_LANGUAGE);
        String target = receivedIntent.getStringExtra(ProfileActivity.TARGET_LANGUAGE);
        String speech = receivedIntent.getStringExtra(ProfileActivity.SPEECH_LANGUAGE);
        int shakeToSpeak = receivedIntent.getIntExtra(ProfileActivity.SHAKE_TO_SPEAK, 0);
        int longPressCopy = receivedIntent.getIntExtra(ProfileActivity.LONG_PRESS_COPY, 0);

        userNameET.setText(name);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, languages);
        userSourceLanguageSpinner.setAdapter(adapter);
        userTargetLanguageSpinner.setAdapter(adapter);
        userSpeechLanguageSpinner.setAdapter(adapter);


        //Get language and set appropriate value to each spinner
        if (!source.isEmpty()) {
            userSourceLanguageSpinner.setSelection(languages.indexOf(source));
        }
        if (!target.isEmpty()) {
            userTargetLanguageSpinner.setSelection(languages.indexOf(target));
        }
        if (!speech.isEmpty()) {
            userSpeechLanguageSpinner.setSelection(languages.indexOf(speech));
        }

        if (shakeToSpeak == 1) {
            shakeToSpeakCheckBox.setChecked(true);
        } else
            shakeToSpeakCheckBox.setChecked(false);

        if (longPressCopy == 1) {
            longPressCopyCheckBox.setChecked(true);
        } else
            longPressCopyCheckBox.setChecked(false);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

    }

    private void saveProfile() {
        Intent intent = new Intent();
        intent.putExtra(UserTable.NAME, userNameET.getText().toString());
        intent.putExtra(UserTable.TARGET_LANG, userTargetLanguageSpinner.getSelectedItem().toString());
        intent.putExtra(UserTable.SOURCE_LANG, userSourceLanguageSpinner.getSelectedItem().toString());
        intent.putExtra(UserTable.SPEECH_LANG, userSpeechLanguageSpinner.getSelectedItem().toString());


        ContentValues contentValues = new ContentValues();
        contentValues.put(UserTable.NAME, userNameET.getText().toString());
        contentValues.put(UserTable.TARGET_LANG, userTargetLanguageSpinner.getSelectedItem().toString());
        contentValues.put(UserTable.SOURCE_LANG, userSourceLanguageSpinner.getSelectedItem().toString());
        contentValues.put(UserTable.SPEECH_LANG, userSpeechLanguageSpinner.getSelectedItem().toString());

        if (shakeToSpeakCheckBox.isChecked()) {
            intent.putExtra(UserTable.SHAKE_TO_SPEAK, 1);
            contentValues.put(UserTable.SHAKE_TO_SPEAK, 1);
        } else {
            intent.putExtra(UserTable.SHAKE_TO_SPEAK, 0);
            contentValues.put(UserTable.SHAKE_TO_SPEAK, 0);
        }

        if (longPressCopyCheckBox.isChecked()) {
            intent.putExtra(UserTable.LONG_PRESS_COPY, 1);
            contentValues.put(UserTable.LONG_PRESS_COPY, 1);
        } else {
            intent.putExtra(UserTable.LONG_PRESS_COPY, 0);
            contentValues.put(UserTable.LONG_PRESS_COPY, 0);
        }

        getContentResolver().update(MyContentProvider.CONTENT_URI, contentValues, "_id = ?", new String[] {"1"});

        setResult(RESULT_OK, intent);
        finish();
    }
}
