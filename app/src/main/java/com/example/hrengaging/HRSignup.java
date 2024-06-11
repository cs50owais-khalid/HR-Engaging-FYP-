package com.example.hrengaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HRSignup extends AppCompatActivity {
    TextView textViewLogin;
    EditText Fullname, Email, Password, Confirmpassword;
    Button BtnSignup;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    DatabaseReference HRReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrsignup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textViewLogin = findViewById(R.id.logintxt);
        Fullname = findViewById(R.id.fullname);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Confirmpassword = findViewById(R.id.confirmpassword);
        BtnSignup = findViewById(R.id.btnSignup);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        HRReference = FirebaseDatabase.getInstance().getReference().child("HR");

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HRSignup.this, HRLogin.class));
            }
        });

        BtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforAuth();
            }
        });
    }

    private void PerforAuth() {
        // Get user input
        String fullname = Fullname.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String confirmpassword = Confirmpassword.getText().toString();

        // Validate user input
        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(HRSignup.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(HRSignup.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmpassword)) {
            Toast.makeText(HRSignup.this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        progressDialog.setMessage("Please Wait While Registering...");
        progressDialog.setTitle("Registration");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Authentication successful, get the user ID
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a user in the Realtime Database
                            helperClass newUser = new helperClass(userId, fullname, email, password, confirmpassword);
                            HRReference.child(userId).setValue(newUser);

                            // Send verification email
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(HRSignup.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                                                sendUserToNextActivity();
                                            } else {
                                                Toast.makeText(HRSignup.this, "Verification email not sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Authentication failed
                            Toast.makeText(HRSignup.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void sendUserToNextActivity() {
        Intent intent = new Intent(HRSignup.this, HRLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
