package com.example.hrengaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MultiLogin extends AppCompatActivity {

    TextView hrApply;
    TextView userApply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_login);
        hrApply = findViewById(R.id.hrapply);
        userApply = findViewById(R.id.userapply);

        hrApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hrintent = new Intent(MultiLogin.this, HRLogin.class);
                startActivity(hrintent);
            }
        });

        userApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userintent = new Intent(MultiLogin.this, Login.class);
                startActivity(userintent);
            }
        });
    }
}