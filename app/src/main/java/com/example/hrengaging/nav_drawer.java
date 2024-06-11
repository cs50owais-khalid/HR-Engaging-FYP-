package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class nav_drawer extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        TextView profileSettingsTextView = findViewById(R.id.profilesetting);

        profileSettingsTextView.setOnClickListener(view -> {
            Intent ProfileIntent = new Intent(nav_drawer.this, EditProfile.class);
            startActivity(ProfileIntent);
        });

    }

}

