package com.example.hrengaging;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HREditProfile extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, passwordEditText;
    private Button saveChangesButton;
    private DatabaseReference hrRef;
    private ImageView backBtn;
    private FirebaseAuth firebaseAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hredit_profile);

        // Initialize UI elements
        fullNameEditText = findViewById(R.id.profilename);
        emailEditText = findViewById(R.id.profileemail);
        passwordEditText = findViewById(R.id.profilepassword);
        saveChangesButton = findViewById(R.id.btnsave);
        backBtn = findViewById(R.id.backtoDashboard);
        emailEditText.setEnabled(false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            userID = firebaseAuth.getCurrentUser().getUid();
            hrRef = FirebaseDatabase.getInstance().getReference("HR").child(userID);

            // Load current user details
            loadUserProfile();
        } else {
            Toast.makeText(HREditProfile.this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if the user is not logged in
        }

        // Set up save button click listener
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadUserProfile() {
        hrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> userProfile = (HashMap<String, Object>) snapshot.getValue();
                    if (userProfile != null) {
                        fullNameEditText.setText((String) userProfile.get("fullname"));
                        emailEditText.setText((String) userProfile.get("email"));
                        passwordEditText.setText((String) userProfile.get("password"));
                    }
                } else {
                    Toast.makeText(HREditProfile.this, "Profile data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HREditProfile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String fullName = fullNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("fullname", fullName);
        map.put("password", password);

        // Update user profile with the new data
        hrRef.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HREditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HREditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
