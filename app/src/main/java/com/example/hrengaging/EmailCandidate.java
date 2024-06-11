package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmailCandidate extends AppCompatActivity {

    ImageView backBtn;
    EditText email, subject, body;
    Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_candidate);

        backBtn = findViewById(R.id.backButton);
        email = findViewById(R.id.editemail);
        subject = findViewById(R.id.editsubject);
        body = findViewById(R.id.editbody);
        send_btn = findViewById(R.id.send_email);

        String userId = getIntent().getStringExtra("userId");
        String emailAddress = getIntent().getStringExtra("emailAddress"); // Retrieve the email address
        email.setText(emailAddress);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editemail, editsubject, editbody;
                editsubject=email.getText().toString();
                editemail=subject.getText().toString();
                editbody=body.getText().toString();

                if(editsubject.isEmpty() || editemail.isEmpty() || editbody.isEmpty()){
                    Toast.makeText(EmailCandidate.this,"All fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendEmail(editsubject,editemail, editbody);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void sendEmail(String editemail,String editsubject,String editbody){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{editemail});
        intent.putExtra(Intent.EXTRA_SUBJECT, editsubject);
        intent.putExtra(Intent.EXTRA_TEXT, editbody);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent,"Choose email client:"));
    }
}