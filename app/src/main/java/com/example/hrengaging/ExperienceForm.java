package com.example.hrengaging;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ExperienceForm extends AppCompatActivity {
     TextView startingDate;
     TextView endingDate;
     ImageView startingCalendar;
     ImageView endingCalendar;
     EditText companyName;
     EditText jobTitle;
     TextView textUploadLogo;
     ImageView companyLogo;
     Spinner employmentType;
     Spinner seniorityLevel;
     StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedEmploymentType;
    private FirebaseAuth auth;
    private DatabaseReference userExperience;
    private DatabaseReference userReference;
    Button btnSave;

    private boolean isLogoUploaded = false;
    private String imageUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_form);

        storageReference = FirebaseStorage.getInstance().getReference("Experience_Company_Logo");
        userReference = FirebaseDatabase.getInstance().getReference("users");
        userExperience = FirebaseDatabase.getInstance().getReference("Experience");
        companyName = findViewById(R.id.companyName);
        jobTitle = findViewById(R.id.job_title);
        employmentType = findViewById(R.id.employmentTypeSpinner);
        seniorityLevel = findViewById(R.id.seniorityLevelSpinner);
        startingDate = findViewById(R.id.startingDate);
        endingDate = findViewById(R.id.endingDate);
        startingCalendar = findViewById(R.id.Calendar);
        endingCalendar = findViewById(R.id.endingCalendar);
        companyLogo = findViewById(R.id.companyLogo);
        textUploadLogo = findViewById(R.id.textUploadLogo);
        btnSave = findViewById(R.id.btnSave);


//        Toast.makeText(ExperienceForm.this, "Enter all the fields", Toast.LENGTH_SHORT).show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // If no user is found, start MultiLogin activity
            Intent intent = new Intent(ExperienceForm.this, MultiLogin.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent returning to it when pressing back
            return; // Return from onCreate to prevent executing further code
        } else if (user != null) {
            String userID = user.getUid();
            Toast.makeText(ExperienceForm.this, "User ID: " + userID, Toast.LENGTH_SHORT).show();
        }

        employmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedEmploymentType = position == 0 ? "None Selected" : parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedEmploymentType = "None Selected";
            }
        });


        ArrayAdapter<CharSequence> employmentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.employment_types,
                android.R.layout.simple_spinner_item
        );
        employmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employmentType.setAdapter(new Adapter(this, android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.employment_types)));


        startingCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDiaglog();
            }
        });


        endingCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExperience();
            }
        });

        companyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }


    private void createExperience() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(ExperienceForm.this, "No user found!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        String CompanyName = companyName.getText().toString();
        String JobTitle = jobTitle.getText().toString();
        String StartingDate = startingDate.getText().toString();
        String EndingDate = endingDate.getText().toString();

        if (CompanyName.isEmpty() || JobTitle.isEmpty() || StartingDate.isEmpty() || EndingDate.isEmpty()) {
            Toast.makeText(ExperienceForm.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedEmploymentType.equals("None Selected")){
            Toast.makeText(ExperienceForm.this, "Select all options", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isLogoUploaded) {
            Toast.makeText(ExperienceForm.this, "Please upload the company logo", Toast.LENGTH_SHORT).show();
            return;
        }

        String expId = userExperience.push().getKey();

        // Extract text from TextViews before passing them to the constructor
        String startingDateString = startingDate.getText().toString();
        String endingDateString = endingDate.getText().toString();

        // Create instance of ExpHelperClass using extracted text
        ExpHelperClass newExp = new ExpHelperClass(expId, userId, CompanyName, JobTitle, selectedEmploymentType, startingDateString, endingDateString);
        newExp.setImageUrl(imageUrl);
        userExperience.child(expId).setValue(newExp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ExperienceForm.this, "uploaded by" + userId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExperienceForm.this, "Failed to Add", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clear input fields after posting
        jobTitle.setText("");
        companyName.setText("");
        employmentType.setSelection(0);
        startingDate.setText("");
        endingDate.setText("");
        imageUrl = null;
        companyLogo = null;
        textUploadLogo.setText("Upload Logo");
    }




    private void openDiaglog(){
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startingDate.setText(String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth)+"-"+String.valueOf(year));
            }
        },2022,0,15);
        dialog.show();
    }


    private void openCustomDialog() {
        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_date_picker, null);

        // Find DatePicker and Button in the custom layout
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TextView buttonPresent = dialogView.findViewById(R.id.buttonPresent);
        TextView buttonOk = dialogView.findViewById(R.id.buttonOk);
        TextView buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        // Create and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set a listener for the "Present" button
        buttonPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set the startingDate TextView to "Present"
                endingDate.setText("Present");
                // Dismiss the dialog
                dialog.dismiss();
            }
        });


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date from the DatePicker
                int year = datePicker.getYear();
                int monthOfYear = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                endingDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                dialog.dismiss();
            }
        });


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set a listener for the DatePicker
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Set the startingDate TextView to the selected date
                    endingDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                    // Dismiss the dialog
                    dialog.dismiss();
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
            String jobId = userReference.push().getKey();
            uploadImage(jobId, imageUri, new CreateJob.ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Handle successful image upload, e.g., save the image URL
                    Toast.makeText(ExperienceForm.this, "Company Logo Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    // Replace the companyLogo with imageUrl
                    Picasso.get().load(imageUrl).into(companyLogo);

                    // Set the flag to indicate that the logo is uploaded
                    isLogoUploaded = true;
                    textUploadLogo.setText("Uploaded Logo Successfully!");

                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle image upload failure
                    Toast.makeText(ExperienceForm.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(String jobId, Uri imageUri, CreateJob.ImageUploadCallback callback) {
        StorageReference imageRef = storageReference.child("job_images/" + jobId + getFileExtension(imageUri));

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



    public class Adapter extends ArrayAdapter<CharSequence> {
        public Adapter(Context context, int resource, CharSequence[] items) {
            super(context, resource, items);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);

            if (position == 0) {
                view.setEnabled(false);
                view.setOnClickListener(null);
            } else {
                view.setEnabled(true);
            }

            return view;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            if (position == 0) {
                ((TextView) view.findViewById(android.R.id.text1)).setGravity(View.TEXT_ALIGNMENT_CENTER);
            }

            return view;
        }
    }
}
