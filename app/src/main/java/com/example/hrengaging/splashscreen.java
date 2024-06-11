package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    // User is logged in, start MainActivity
                    startActivity(new Intent(splashscreen.this, MainActivity.class));
                } else {
                    // User is not logged in, start MultiLoginActivity
                    startActivity(new Intent(splashscreen.this, MultiLogin.class));
                }
                finish();
            }
        }, 2000);
    }
}