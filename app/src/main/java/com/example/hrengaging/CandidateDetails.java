package com.example.hrengaging;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class CandidateDetails extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;

    private TextView userFullName, userEmail, userPhone, userCommute, userWillingTime, userDescription;
    private ImageView DownloadCV,backBtn;
    private Button emailBtn;
    private DatabaseReference jobsAppliedRef;
    private String cvUrl; // URL of the CV
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_details);

        // Initialize UI elements
        userFullName = findViewById(R.id.userFullName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userCommute = findViewById(R.id.userCommute);
        userWillingTime = findViewById(R.id.userWillingTime);
        userDescription = findViewById(R.id.userDescription);
        DownloadCV = findViewById(R.id.downloadCV);
        backBtn = findViewById(R.id.backtoDashboard);
        emailBtn = findViewById(R.id.btnEmail);

        // Get the job ID and user ID from the intent
        String jobId = getIntent().getStringExtra("jobId");
        String userId = getIntent().getStringExtra("userId");

        // Log the values to check if they are being received
        Log.d("CandidateDetails", "jobId: " + jobId);
        Log.d("CandidateDetails", "userId: " + userId);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CandidateDetails.this, EmailCandidate.class);
                intent.putExtra("userId", userID);
                intent.putExtra("emailAddress", userEmail.getText().toString()); // Add this line
                startActivity(intent);
            }
        });


        // Initialize Firebase reference
        jobsAppliedRef = FirebaseDatabase.getInstance().getReference("Jobs Applied");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        // Fetch candidate details
        if (jobId != null && userId != null) {
            fetchCandidateDetails(jobId, userId);
        } else {
            Toast.makeText(this, "Job ID or User ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Set up the download CV button click listener
        DownloadCV.setOnClickListener(v -> {
            if (cvUrl != null && !cvUrl.isEmpty()) {
                if (checkPermission()) {
                    downloadCV(cvUrl);
                } else {
                    requestPermission();
                }
            } else {
                Toast.makeText(CandidateDetails.this, "CV URL is missing", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void fetchCandidateDetails(String jobId, String userId) {
        jobsAppliedRef.child(jobId).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, Object> candidateMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (candidateMap != null) {
                        userFullName.setText((String) candidateMap.get("fullName"));
                        userEmail.setText((String) candidateMap.get("email"));
                        userPhone.setText((String) candidateMap.get("phoneNumber"));
                        userCommute.setText((String) candidateMap.get("commuteDecision"));
                        userWillingTime.setText((String) candidateMap.get("willingTime"));
                        userDescription.setText((String) candidateMap.get("description"));
                        cvUrl = (String) candidateMap.get("documentUri");
                        Log.d("CandidateDetails", "cvUrl: " + cvUrl);

//
                    }
                } else {
                    Toast.makeText(CandidateDetails.this, "No data found for this candidate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CandidateDetails.this, "Failed to fetch candidate details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(CandidateDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (cvUrl != null) {
                    downloadCV(cvUrl);
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadCV(String cvUrl) {
        if (cvUrl != null && !cvUrl.isEmpty()) {
            if (checkPermission()) {
                downloadFile(cvUrl);
            } else {
                requestPermission();
            }
        } else {
            Toast.makeText(this, "CV URL is invalid", Toast.LENGTH_SHORT).show();
        }
    }


    private void downloadFile(String cvUrl) {
        if (cvUrl != null && !cvUrl.isEmpty()) {
            if (checkPermission()) {
                // Check if cvUrl is a content URI
                if (cvUrl.startsWith("content://")) {
                    downloadContentUriCV(cvUrl);
                } else {
                    // Use your existing downloadFile implementation for URLs
                    downloadFile(cvUrl);
                }
            } else {
                requestPermission();
            }
        } else {
            Toast.makeText(this, "CV URL is invalid", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadContentUriCV(String contentUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = Uri.parse(contentUri);

            // Open an InputStream using the ContentResolver
            InputStream input = contentResolver.openInputStream(uri);

            // Rest of your download logic using input stream and FileOutputStream
            // ... (similar to the existing downloadFile implementation)

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            runOnUiThread(() -> Toast.makeText(CandidateDetails.this, "Failed to download CV: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }




}
