package com.example.hrengaging;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditEdu extends AppCompatActivity {

    TextView startingDate, endingDate, textUploadLogo;
    ImageView startingCalendar, endingCalendar, InstitutionLogo, deleteBtn;
    EditText institutionName, educationLevel;
    Spinner educationType;
    StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference userEducation, userReference;
    Button btnSave;
    EduHelperClass eduItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_edu);

        storageReference = FirebaseStorage.getInstance().getReference("Institution_Logo");
        userReference = FirebaseDatabase.getInstance().getReference("users");
        userEducation = FirebaseDatabase.getInstance().getReference("Education");
        institutionName = findViewById(R.id.companyName);
        educationLevel = findViewById(R.id.job_title);
        educationType = findViewById(R.id.employmentTypeSpinner);
        startingDate = findViewById(R.id.startingDate);
        endingDate = findViewById(R.id.endingDate);
        startingCalendar = findViewById(R.id.Calendar);
        endingCalendar = findViewById(R.id.endingCalendar);
        InstitutionLogo = findViewById(R.id.companyLogo);
        textUploadLogo = findViewById(R.id.textUploadLogo);
        btnSave = findViewById(R.id.btnSave);
        deleteBtn = findViewById(R.id.dltBtn);

        // Set the adapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.education_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationType.setAdapter(adapter);

        Intent intent = getIntent();
        eduItem = (EduHelperClass) intent.getSerializableExtra("EduHelperClass");

        if (eduItem != null) {
            displayEduDetails(eduItem);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUpdatedEducation();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

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

            InstitutionLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageChooser();
                }
            });
        } else {
            Toast.makeText(this, "Error: Education item is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        eduItem.setImageUrl(imageUrl);
                        Picasso.get().load(imageUrl).into(InstitutionLogo);
                        textUploadLogo.setText("Uploaded Successfully!");
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditEdu.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void displayEduDetails(EduHelperClass eduItem) {
        institutionName.setText(eduItem.getInstitutionName());
        educationLevel.setText(eduItem.getEducationalLevel());
        startingDate.setText(eduItem.getStartingDate());
        endingDate.setText(eduItem.getEndingDate());
        if (eduItem.getImageUrl() != null && !eduItem.getImageUrl().isEmpty()) {
            Picasso.get().load(eduItem.getImageUrl()).into(InstitutionLogo);
        }

        fetchAndSetInstitutionType(eduItem.getInstitutionType());
    }

    private void fetchAndSetInstitutionType(String institutionTypeStr) {
        userEducation.child(eduItem.getEduId()).child("institutionType").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String institutionTypeValue = task.getResult().getValue(String.class);
                if (institutionTypeValue != null) {
                    setSpinnerSelection(educationType, institutionTypeValue);
                }
            } else {
                Toast.makeText(EditEdu.this, "Failed to fetch institution type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.education_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (value != null) {
            int spinnerPosition = adapter.getPosition(value);
            spinner.setSelection(spinnerPosition);
        }
    }

    private void saveUpdatedEducation() {
        try {
            String updatedInstitutionName = institutionName.getText().toString();
            String updatedEducationLevel = educationLevel.getText().toString();
            String updatedStartingDate = startingDate.getText().toString();
            String updatedEndingDate = endingDate.getText().toString();
            String updatedEducationType = educationType.getSelectedItem().toString();

            if (eduItem != null) {
                eduItem.setInstitutionName(updatedInstitutionName);
                eduItem.setEducationalLevel(updatedEducationLevel);
                eduItem.setStartingDate(updatedStartingDate);
                eduItem.setEndingDate(updatedEndingDate);
                eduItem.setInstitutionType(updatedEducationType);

                userEducation.child(eduItem.getEduId()).setValue(eduItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditEdu.this, "Education updated successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after updating
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update education", e);
                            Toast.makeText(EditEdu.this, "Failed to update education: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e(TAG, "eduItem is null during save");
                Toast.makeText(this, "Error: Education item is null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveUpdatedEducation: Error updating education", e);
            Toast.makeText(this, "Error updating education: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this education item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEducationItem();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteEducationItem() {
        if (eduItem != null && eduItem.getEduId() != null) {
            userEducation.child(eduItem.getEduId()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditEdu.this, "Education item deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after deletion
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditEdu.this, "Failed to delete education item", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Invalid education item", Toast.LENGTH_SHORT).show();
        }
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
}