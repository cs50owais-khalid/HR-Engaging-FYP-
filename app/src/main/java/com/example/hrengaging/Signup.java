package com.example.hrengaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Signup extends AppCompatActivity {
    TextView textViewLogin;
    EditText Fullname, Email, Password, Confirmpassword;
    Button BtnSignup;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textViewLogin = findViewById(R.id.logintxt);
        Fullname = findViewById(R.id.fullname);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Confirmpassword = findViewById(R.id.confirmpassword);
        BtnSignup = findViewById(R.id.btnSignup);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");



        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Login.class));
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
        String fullname = Fullname.getText().toString();
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String confirmpassword = Confirmpassword.getText().toString();

        if (fullname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()) {
            Toast.makeText(Signup.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Signup.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmpassword)) {
            Toast.makeText(Signup.this, "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Please Wait While Registering...");
        progressDialog.setTitle("Registration");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // User has been successfully created
                    String userId = mAuth.getCurrentUser().getUid(); // Retrieve the UID
                    // Create a user object and store it in the Realtime Database
                    helperClass newUser = new helperClass(userId, fullname, email, password, confirmpassword);
                    usersReference.child(userId).setValue(newUser);
                    sendUserToNextActivity();
                    Toast.makeText(Signup.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Signup.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(Signup.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
