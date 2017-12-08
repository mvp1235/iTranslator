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
    private Button editProfileBtn, logOutBtn, getCurrentLocationBtn;
    private SignInButton googleSignInBtn;
    private TextView locationTV;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;  //display current progress dialog

    private LocationManager locationManager;    //used to get current location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        //Listener for authentication changes
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {    //user is signed in
                    googleSignInBtn.setVisibility(View.GONE);
                    logOutBtn.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {    //user is logged out
                    googleSignInBtn.setVisibility(View.VISIBLE);
                    logOutBtn.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
            }
        };

        //Referencing to UI elements
        userNameTV = findViewById(R.id.userName);
        userIdTV = findViewById(R.id.userID);
        userSourceLanguageTV = findViewById(R.id.userSourceLanguage);
        userTargetLanguageTV = findViewById(R.id.userTargetLanguage);
        userSpeechLanguageTV = findViewById(R.id.userSpeechLanguage);
        userShakeToSpeakTV = findViewById(R.id.userShakeToSpeak);
        userLongPressCopyTV = findViewById(R.id.userLongPressCopy);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        logOutBtn = findViewById(R.id.logOutBtn);
        getCurrentLocationBtn = findViewById(R.id.getCurrentLocationBtn);
        locationTV = findViewById(R.id.locationTV);
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //build the google sign in activity
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection failed... PLease try again later.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        //Only enable the get current location feature if user is logged in through Google Sign-in
        getCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationTV.setText("You need to enable GPS to use the current location feature.");
                    locationTV.setTextColor(Color.RED);
                    buildAlertMessageNoGps();
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (mAuth.getCurrentUser() != null) {
                        getLocation();
                    } else {
                        locationTV.setText("You need to sign in for unlocking the current location feature.");
                        locationTV.setTextColor(Color.RED);
                    }
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Obtain current location, and call geo-location to retrieve the nearest estimate address
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);

        } else {
            //Three different modes are used, in case one doesn't work or is not supported, the rest can be used
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            double lat = 0;
            double lng = 0;

            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            } else  if (location1 != null) {
                lat = location1.getLatitude();
                lng = location1.getLongitude();
            } else  if (location2 != null) {
                lat = location2.getLatitude();
                lng = location2.getLongitude();
            } else {
                Toast.makeText(this,"Cannot retrieved your current location.",Toast.LENGTH_SHORT).show();
            }

            if (lat != 0 && lng != 0) {
                String locationText = "Your current location is"+ "\n" + "Latitude = " + String.valueOf(lat)
                        + "\n" + "Longitude = " + String.valueOf(lng);

                //Retrieve physical address of a LatLng
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            // In this sample, get just a single address.
                            1);
                    String address = addresses.get(0).getAddressLine(0);
                    locationText += "\n\n" + "Nearest estimated address:\n";
                    locationTV.setText(locationText + address);
                    locationTV.setTextColor(Color.BLACK);

                } catch (IOException ioException) {
                    // Catch network or other I/O problems.

                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                }
            }
        }
    }

    /**
     * Show message to user if location is turned off, and lead them to the location setting on device
     */
    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Turn on your GPS?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {    //location request is successful
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Starts the Google sign in activity
     */
    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Sign users out
     */
    private void signOut() {
        progressDialog.setMessage("Signing out...");
        progressDialog.show();
        mAuth.signOut();
    }

    /**
     * Authenticate provided account to Google
     * @param acct account to be authenticate
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ProfileActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                        progressDialog.dismiss();

                        // ...
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
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
