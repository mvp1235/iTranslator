package com.example.mvp.itranslator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Profile Activity, where user can see their personal information and settings
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "GOOGLE AUTH";
    private static final int EDIT_PROFILE_REQUEST_CODE = 500;
    private static final int LOCATION_REQUEST = 501;
    private static final int RC_SIGN_IN = 600;

    public static String NAME = "name";
    public static String SOURCE_LANGUAGE = "sourceLanguage";
    public static String TARGET_LANGUAGE = "targetLanguage";
    public static String SPEECH_LANGUAGE = "speechLanguage";
    public static String SHAKE_TO_SPEAK = "shakeToSpeak";
    public static String LONG_PRESS_COPY = "longPressCopy";

    private TextView userNameTV, userIdTV, userSourceLanguageTV, userTargetLanguageTV, userSpeechLanguageTV, userShakeToSpeakTV, userLongPressCopyTV;
    private Button editProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Referencing to UI elements
        userNameTV = findViewById(R.id.userName);
        userIdTV = findViewById(R.id.userID);
        userSourceLanguageTV = findViewById(R.id.userSourceLanguage);
        userTargetLanguageTV = findViewById(R.id.userTargetLanguage);
        userSpeechLanguageTV = findViewById(R.id.userSpeechLanguage);
        userShakeToSpeakTV = findViewById(R.id.userShakeToSpeak);
        userLongPressCopyTV = findViewById(R.id.userLongPressCopy);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        //Retrieve all profile information from database
        String id = getDatabaseColumnValue(UserTable._ID);
        String name = getDatabaseColumnValue(UserTable.NAME);
        String sourceLang = getDatabaseColumnValue(UserTable.SOURCE_LANG);
        String targetLang = getDatabaseColumnValue(UserTable.TARGET_LANG);
        String speechLang = getDatabaseColumnValue(UserTable.SPEECH_LANG);
        String shakeToSpeak = getDatabaseColumnValue(UserTable.SHAKE_TO_SPEAK);
        String longPressCopy = getDatabaseColumnValue(UserTable.LONG_PRESS_COPY);

        //Fill in all fields
        if (id != null)
            userIdTV.setText(id);
        if (name != null)
            userNameTV.setText(name);
        if (sourceLang != null)
            userSourceLanguageTV.setText(sourceLang);
        if (targetLang != null)
            userTargetLanguageTV.setText(targetLang);
        if (speechLang != null)
            userSpeechLanguageTV.setText(speechLang);

        //if shake to speak feature is enable
        if (shakeToSpeak != null && shakeToSpeak.equalsIgnoreCase("1"))
            userShakeToSpeakTV.setText("Enabled");
        else
            userShakeToSpeakTV.setText("Disabled");

        //if long press copy is enabled
        if (longPressCopy != null && longPressCopy.equalsIgnoreCase("1"))
            userLongPressCopyTV.setText("Enabled");
        else
            userLongPressCopyTV.setText("Disabled");

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
    }

    /**
     * Obtain data from the fields and update profile on database
     */
    private void editProfile() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        intent.putExtra(NAME, userNameTV.getText().toString());
        intent.putExtra(SOURCE_LANGUAGE, userSourceLanguageTV.getText().toString());
        intent.putExtra(TARGET_LANGUAGE, userTargetLanguageTV.getText().toString());
        intent.putExtra(SPEECH_LANGUAGE, userSpeechLanguageTV.getText().toString());

        String shakeToSpeak = getDatabaseColumnValue(UserTable.SHAKE_TO_SPEAK);
        String longPressCopy = getDatabaseColumnValue(UserTable.LONG_PRESS_COPY);
        intent.putExtra(SHAKE_TO_SPEAK, Integer.parseInt(shakeToSpeak));
        intent.putExtra(LONG_PRESS_COPY, Integer.parseInt(longPressCopy));
        startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If editing activity succesfully returns, take the returned intent data and update current fields
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            userNameTV.setText(data.getStringExtra(UserTable.NAME));
            userSourceLanguageTV.setText(data.getStringExtra(UserTable.SOURCE_LANG));
            userTargetLanguageTV.setText(data.getStringExtra(UserTable.TARGET_LANG));
            userSpeechLanguageTV.setText(data.getStringExtra(UserTable.SPEECH_LANG));

            if (data.getIntExtra(UserTable.SHAKE_TO_SPEAK, 0) == 1)
                userShakeToSpeakTV.setText("Enabled");
            else
                userShakeToSpeakTV.setText("Disabled");

            if (data.getIntExtra(UserTable.LONG_PRESS_COPY, 0) == 1)
                userLongPressCopyTV.setText("Enabled");
            else
                userLongPressCopyTV.setText("Disabled");
        }
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

}
