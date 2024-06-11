package com.example.hrengaging;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ApplyJob extends AppCompatActivity {

    private static final int PICK_DOCUMENT_REQUEST = 2;
    private Uri documentUri;
    private boolean isDocumentUploaded = false;

    EditText fullNameEditText, emailEditText, phoneNumberEditText, willingTime, describe;
    Button Apply;
    FrameLayout uploadSection;
    private DatabaseReference userReference;
    private DatabaseReference jobReference;
    RadioButton option1, option2;
    RadioGroup commuteRadioGroup;
    ImageView crossIcon;
    private TextView uploadText;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        userReference = FirebaseDatabase.getInstance().getReference("users");
        jobReference = FirebaseDatabase.getInstance().getReference().child("jobs");



        String jobId = getIntent().getStringExtra("JobId");
        Toast.makeText(ApplyJob.this, "JobID: " + jobId, Toast.LENGTH_SHORT).show();


        fullNameEditText = findViewById(R.id.fullName);
        emailEditText = findViewById(R.id.emailAddress);
        phoneNumberEditText = findViewById(R.id.phone);
        willingTime = findViewById(R.id.time);
        describe = findViewById(R.id.job_desribe);
        Apply = findViewById(R.id.btnApply);
        uploadSection = findViewById(R.id.UploadSection);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        commuteRadioGroup = findViewById(R.id.commuteRadioGroup);
        uploadText = findViewById(R.id.uploadText);
        crossIcon = findViewById(R.id.cross_icon);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid(); // Use the class-level variable



        fullNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);

//        Toast.makeText(ApplyJob.this,"JobID " + jobId,Toast.LENGTH_SHORT).show();

        userReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullname").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    fullNameEditText.setText(fullName);
                    emailEditText.setText(email);
                } else {
                    Toast.makeText(ApplyJob.this, "No account found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        DatabaseReference userJobApplicationReference = _firebase.getReference("Jobs Applied").child(jobId).child(currentUserId);

        userJobApplicationReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle new child added (if needed)
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle child data changes (if needed)
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle child removed (if needed)
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle child moved (if needed)
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        uploadSection.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf"); // You can adjust the MIME type based on the type of documents you want to allow
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
        });


        Apply.setOnClickListener(view -> {
            if (isDocumentUploaded) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyJob.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to submit the application?");
                builder.setPositiveButton("Yes", (dialog, which) -> {


                    JobApplication jobApplication = new JobApplication();
                    jobApplication.setUserId(currentUserId);
                    jobApplication.setJobId(jobId);
                    jobApplication.setFullName(fullNameEditText.getText().toString());
                    jobApplication.setEmail(emailEditText.getText().toString());
                    jobApplication.setPhoneNumber(phoneNumberEditText.getText().toString());
                    jobApplication.setWillingTime(willingTime.getText().toString());
                    jobApplication.setDescription(describe.getText().toString());

                    int selectedRadioButtonId = commuteRadioGroup.getCheckedRadioButtonId();

                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        String commuteDecision = selectedRadioButton.getText().toString();
                        jobApplication.setCommuteDecision(commuteDecision);

                        // Set document URI in JobApplication
                        jobApplication.setDocumentUri(documentUri.toString());

                        // Save job application in Firebase under the corresponding job ID and user ID
                        userJobApplicationReference.setValue(jobApplication);

                        Intent intent = new Intent(ApplyJob.this, SuccessfullApplyJob.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ApplyJob.this, "Please select commute decision", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    // User clicked No, do nothing or handle accordingly
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(ApplyJob.this, "Please upload a document", Toast.LENGTH_SHORT).show();
            }
        });



        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Limit the length of the phone number to 11 digits
                if (editable.length() > 11) {
                    String newPhoneNumber = editable.toString().substring(0, 11);
                    phoneNumberEditText.setText(newPhoneNumber);
                    phoneNumberEditText.setSelection(11); // Set the cursor position to the end
                }
            }
        });

        willingTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Limit the length of the phone number to 11 digits
                if (editable.length() > 0) {
                    int enteredValue = Integer.parseInt(editable.toString());
                    if (enteredValue > 9) {
                        // If more than 9, set to 9
                        willingTime.setText("9");
                        willingTime.setSelection(1); // Set the cursor position to the end
                    } else if (enteredValue < 0) {
                        // If less than 0, set to 0
                        willingTime.setText("0");
                        willingTime.setSelection(1); // Set the cursor position to the end
                    }
                }
            }
        });

        crossIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove the uploaded document logic here

                // Update UI accordingly (hide the cross icon, update text, etc.)
                crossIcon.setVisibility(View.GONE);
                uploadText.setText("Upload Resume");
                isDocumentUploaded = false;
                documentUri = null;
                Toast.makeText(ApplyJob.this, "Document Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            documentUri = data.getData();
            isDocumentUploaded = true;

            // Update the TextView to indicate successful upload
            uploadText.setText("Resume Uploaded Successfully");

            crossIcon.setVisibility(View.VISIBLE);

            // Upload the document to Firebase Storage
            uploadDocumentToStorage(currentUserId);

            // TODO: Update UI or show document details (optional)
        }
    }

    private void uploadDocumentToStorage(String userId) {
        if (documentUri != null) {
            // Create a unique file name for the document
            String documentPath = "CVs/" + userId + "/" + System.currentTimeMillis() + ".pdf";

            // Get a reference to the Firebase Storage location
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(documentPath);

            // Upload the document to Firebase Storage
            storageRef.putFile(documentUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Document uploaded successfully
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Get the download URL and save it to the Realtime Database
                            saveDocumentUriToDatabase(userId, uri.toString());

                            // Extract document name from URI
                            String documentName = getDocumentNameFromUri(documentUri);

                            // Update the TextView to display the document name
                            TextView uploadText2 = findViewById(R.id.uploadText2);
                            uploadText2.setText(documentName);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Document upload failed
                        Toast.makeText(ApplyJob.this, "Document upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveDocumentUriToDatabase(String userId, String documentUri) {
        DatabaseReference userReference = _firebase.getReference("users");

        // Assuming you have a "documents" node in your user's data structure
        userReference.child(userId).child("documents").setValue(documentUri)
                .addOnSuccessListener(aVoid -> {
                    // Document URI saved successfully
                    Toast.makeText(ApplyJob.this, "Document URI saved to database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to save document URI
                    Toast.makeText(ApplyJob.this, "Failed to save document URI to database", Toast.LENGTH_SHORT).show();
                });
    }


    private String getDocumentNameFromUri(Uri uri) {
        String documentName = "";
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                documentName = cursor.getString(nameIndex);
                cursor.close();
            }
        }
        return documentName;
    }

    private void saveDocumentUriToDatabase(String jobId, String userId, String documentUri) {
        DatabaseReference userJobApplicationReference = _firebase.getReference("Jobs Applied").child(jobId).child(userId);

        // Save the document URI under the corresponding job ID and user ID in the "Jobs Applied" table
        userJobApplicationReference.child("documentUri").setValue(documentUri)
                .addOnSuccessListener(aVoid -> {
                    // Document URI saved successfully
                    Toast.makeText(ApplyJob.this, "Document URI saved to database", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to save document URI
                    Toast.makeText(ApplyJob.this, "Failed to save document URI to database", Toast.LENGTH_SHORT).show();
                });
    }



}
