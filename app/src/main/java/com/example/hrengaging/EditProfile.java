package com.example.hrengaging;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    EditText fullNameEditText, emailEditText, passwordEditText;
    Button saveChangesButton;
    CircleImageView userImage;
    ImageView addImage, backBtn;
    Spinner citySpinner;
    String selectedCity;
    String imageUrl;
    DatabaseReference userReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userReference = FirebaseDatabase.getInstance().getReference("users");
        fullNameEditText = findViewById(R.id.profilename);
        emailEditText = findViewById(R.id.profileemail);
        passwordEditText = findViewById(R.id.profilepassword);
        saveChangesButton = findViewById(R.id.btnsave);
        userImage = findViewById(R.id.profileImageView);
        addImage = findViewById(R.id.addJobImageView);
        backBtn = findViewById(R.id.backtoDashboard);
        citySpinner = findViewById(R.id.citySpinner);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        emailEditText.setEnabled(false);

        String userID = firebaseAuth.getCurrentUser().getUid();
        Toast.makeText(EditProfile.this, "UserID: " + userID, Toast.LENGTH_SHORT).show();

        DatabaseReference usersRef = _firebase.getReference("users");
        DatabaseReference hrRef = _firebase.getReference("HR");



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        // Set a listener to handle the selected item from the spinner
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected city
                selectedCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
                selectedCity = ""; // or set a default value
            }
        });
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    final HashMap<String, Object> _childValue = dataSnapshot.getValue(_ind);

                    if (_childValue != null && _childValue.get("userId") != null) {
                        if (firebaseAuth.getCurrentUser().getUid().equals(_childValue.get("userId").toString())) {
                            Object fullName = _childValue.get("fullname");
                            Object email = _childValue.get("email");
                            Object password = _childValue.get("password");
                            Object imageUrl = _childValue.get("imageUrl");
                            Object city = _childValue.get("city");


                            if (fullName != null) {
                                fullNameEditText.setText(fullName.toString());
                            }
                            if (email != null) {
                                emailEditText.setText(email.toString());
                            }
                            if (password != null) {
                                passwordEditText.setText(password.toString());
                            }
                            if (imageUrl != null) {
                                // Load the image using Picasso
                                Picasso.get().load(imageUrl.toString()).into(userImage);
                                // Set imageUrl to the global variable
                                EditProfile.this.imageUrl = imageUrl.toString();
                            }
                            if (city != null) {
                                int index = ((ArrayAdapter<String>) citySpinner.getAdapter()).getPosition(city.toString());
                                // Set the spinner selection to the index
                                citySpinner.setSelection(index);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                pickImage();
            }
        });
    }
    private void updateUserProfile() {
        String fullName = fullNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Check if an image is uploaded
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // If an image is uploaded, include it in the user data map
            Map<String, Object> map = new HashMap<>();
            map.put("fullname", fullName);
            map.put("password", password);
            map.put("imageUrl", imageUrl);
            map.put("city", selectedCity);

            // Update user profile with the new data
            String userID = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference userRef = _firebase.getReference("users").child(userID);
            userRef.updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

                                Picasso.get().load(imageUrl).into(userImage, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        // Remove the white background after the image is loaded
                                        userImage.setBackgroundResource(0); // Set background resource to null
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Handle any errors
                                    }
                                });
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If no image is uploaded, update user profile without the image URL
            Map<String, Object> map = new HashMap<>();
            map.put("fullname", fullName);
            map.put("password", password);

            // Update user profile with the new data
            String userID = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference userRef = _firebase.getReference("users").child(userID);
            userRef.updateChildren(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String userId = userReference.push().getKey();
            uploadImage(userId, imageUri, new EditProfile.ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Handle successful image upload, e.g., save the image URL
                    Toast.makeText(EditProfile.this, "Profile Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    // Replace the companyLogo with imageUrl
                    Picasso.get().load(imageUrl).into(userImage);

                    // Set the flag to indicate that the logo is uploaded
//                    isLogoUploaded = true;
//                    textUploadLogo.setText("Uploaded Logo Successfully!");

                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle image upload failure
                    Toast.makeText(EditProfile.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(String jobId, Uri imageUri, EditProfile.ImageUploadCallback callback) {
        StorageReference imageRef = storageReference.child("User_images/" + jobId + getFileExtension(imageUri));

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL
                        imageUrl = uri.toString();
                        callback.onSuccess(imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Image upload failed");
                });
    }


    // Callback interface for handling image upload success/failure
    interface ImageUploadCallback {
        void onSuccess(String imageUrl);

        void onFailure(String errorMessage);
    }
}
