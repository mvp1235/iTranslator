package com.example.mvp.itranslator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    private ImageButton translateBtn, conversationBtn, placesBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Referencing to the UI elements for usage
        translateBtn = findViewById(R.id.translateBtn);
        conversationBtn = findViewById(R.id.conversationBtn);
        placesBtn = findViewById(R.id.placesBtn);
        profileBtn = findViewById(R.id.profileBtn);

        setUpClickListeners();
    }

    /**
     * Set up listeners for four buttons mode on home screen
     */
    public void setUpClickListeners() {
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterTranslateMode();
            }
        });

        conversationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterConversatinMode();
            }
        });

        placesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPlacesMode();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterProfileMode();
            }
        });
    }

    /**
     * Take users to translate mode, where they can input text by manually typing or speaking, and the app will translate the input into requested language
     */
    private void enterTranslateMode() {
        Intent intent = new Intent(this, TranslateActivity.class);
        startActivity(intent);
    }

    /**
     * Take users to conversation mode, where the app will await for the users' voice input, automatically translate it to requested language, and speak it out
     */
    private void enterConversatinMode() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    /**
     * Takes users to places mdoe, where they can look for places to visit nearby
     */
    private void enterPlacesMode() {

    }

    /**
     * Take users to profile mode, where they can view/edit their personal information
     */
    private void enterProfileMode() {

    }

}
