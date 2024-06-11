package com.example.hrengaging;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private SharedPreferences mPrefs;

    private CheckBox mCheckBoxRemember;
    private static final String PREFS_NAME = "PrefsFile";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";


    TextView textViewSignup;

    EditText Email, Password;
    Button BtnLogin;
    String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    FirebaseDatabase database;

    ImageView BtnGoogle;


    CheckBox Rememberme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        BtnLogin = findViewById(R.id.btnLogin);
        textViewSignup = findViewById(R.id.Signuptxt);
        BtnGoogle = findViewById(R.id.btnGoogle);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mUser = mAuth.getCurrentUser();





        mCheckBoxRemember=(CheckBox) findViewById(R.id.Rememberme);

        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String storedEmail = mPrefs.getString(PREF_EMAIL, "");
        String storedPassword = mPrefs.getString(PREF_PASSWORD, "");

        if (!storedEmail.isEmpty() && !storedPassword.isEmpty()) {
            Email.setText(storedEmail);
            Password.setText(storedPassword);
        }





        textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });


        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerforLogin();
            }
        });

        BtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,GoogleSignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });



    }

    private void PerforLogin() {
        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Enter Valid Email");
        } else if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        else {

            if (mCheckBoxRemember.isChecked()) {
                // Save the email and password in SharedPreferences
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString(PREF_EMAIL, email);
                editor.putString(PREF_PASSWORD, password);
                editor.apply();
            } else {
                // Clear the stored email and password if "Remember Me" is not checked
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.remove(PREF_EMAIL);
                editor.remove(PREF_PASSWORD);
                editor.apply();
            }
            progressDialog.setMessage("Please Wait While Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();



            try {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            Intent SignIn = new Intent(Login.this,MainActivity.class);
                                            startActivity(SignIn);
                                            Toast.makeText(Login.this,"Successfully Logged in!!",Toast.LENGTH_SHORT).show();
                                            finish();

//                                            String email = snapshot.child(Email).child("email").getValue(String.class);
//                                            String password = snapshot.child(Password).child("password").getValue(String.class);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        } else {
                            // Handle authentication exceptions
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                // Handle invalid password
                                Toast.makeText(Login.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                // Handle other exceptions
                                Toast.makeText(Login.this, "Invalid Email or Password" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, "An error occurred during login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendUserToNextActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    //Check if user is already logged in or not
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (mAuth.getCurrentUser() != null){
//            startActivity(new Intent(Login.this,MainActivity.class));
//            finish();
//        }
//        else {
//            Toast.makeText(Login.this,"You can now Login",Toast.LENGTH_SHORT).show();
//        }
//    }
}