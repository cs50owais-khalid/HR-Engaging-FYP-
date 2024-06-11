package com.example.hrengaging;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AddObjective extends AppCompatActivity {

    EditText objective;
    Button btnSave;
    ImageView deleteBtn;
    DatabaseReference usersRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_objective);
        objective = findViewById(R.id.objective);
        btnSave = findViewById(R.id.btnSave);
        deleteBtn = findViewById(R.id.dltBtn);

        int maxLength = 50;
        objective.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        firebaseAuth = FirebaseAuth.getInstance();

        String userID = getIntent().getStringExtra("UserId");
        if (userID == null) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                userID = currentUser.getUid();

            }
        }

        if (userID != null) {
            Toast.makeText(AddObjective.this, "UserID: " + userID, Toast.LENGTH_SHORT).show();

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUserObjective();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            fetchUserObjective();
        }
        else{
            Toast.makeText(this, "No User ID found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserObjective() {
        String userID = firebaseAuth.getCurrentUser().getUid();

        String objectiveText = objective.getText().toString();
        if (!objectiveText.isEmpty()) {
            usersRef.child(userID).child("Objective").setValue(objectiveText)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddObjective.this, "Objective uploaded successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after updating
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update Objective", e);
                        Toast.makeText(AddObjective.this, "Failed to update Objective: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddObjective.this, "Objective cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserObjective(){

        String userID = firebaseAuth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap<String, Object> userMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (userMap != null && userID.equals(userMap.get("userId"))) {
                        String Objective = (String) userMap.get("Objective");
                        if (Objective != null) {
                            objective.setText(Objective);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteObjective();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteObjective(){
        String userID = firebaseAuth.getCurrentUser().getUid();

        String objectiveText = objective.getText().toString();
        if (!objectiveText.isEmpty()) {
            usersRef.child(userID).child("Objective").removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddObjective.this, "Objective Deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after updating
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to Delete Objective", e);
                        Toast.makeText(AddObjective.this, "Failed to Delete Objective: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddObjective.this, "Objective cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

}