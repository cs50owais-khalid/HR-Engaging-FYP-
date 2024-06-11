package com.example.hrengaging;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditExp extends AppCompatActivity {

    TextView startingDate, endingDate, textUploadLogo;
    ImageView startingCalendar, endingCalendar, deleteBtn, companyLogo;
    EditText companyName, jobTitle;
    Spinner employmentType;
    Button btnSave;
    StorageReference storageReference;
    ExpHelperClass expItem;
    DatabaseReference ExpReference;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exp);

        companyName = findViewById(R.id.companyName);
        jobTitle = findViewById(R.id.job_title);
        employmentType = findViewById(R.id.employmentTypeSpinner);
        startingDate = findViewById(R.id.startingDate);
        endingDate = findViewById(R.id.endingDate);
        startingCalendar = findViewById(R.id.Calendar);
        endingCalendar = findViewById(R.id.endingCalendar);
        companyLogo = findViewById(R.id.companyLogo);
        textUploadLogo = findViewById(R.id.textUploadLogo);
        btnSave = findViewById(R.id.btnSave);
        deleteBtn = findViewById(R.id.dltBtn);
        ExpReference = FirebaseDatabase.getInstance().getReference("Experience");
        storageReference = FirebaseStorage.getInstance().getReference("Company_Logo");


        Intent intent = getIntent();
        expItem = (ExpHelperClass) intent.getSerializableExtra("ExpHelperClass");

        // Set up spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.employment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employmentType.setAdapter(adapter);

        if (expItem != null) {
            displayExpDetails(expItem);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUpdatedExperience();
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
                    openDialog();
                }
            });

            endingCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCustomDialog();
                }
            });

            companyLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageChooser();
                }
            });
        } else {
            Toast.makeText(this, "Error: experience item is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndSetEmploymentType(String employmentTypeStr) {
        ExpReference.child(expItem.getExpId()).child("employmentType").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String employmentType2 = expItem.getEmploymentType();
                String employmentTypeValue = task.getResult().getValue(String.class);
                if (employmentTypeValue != null) {
                    setSpinnerSelection(employmentType, employmentTypeValue);
                    Toast.makeText(EditExp.this, "employment type is: "+ employmentType2, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditExp.this, "Failed to fetch employment type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (value != null) {
            int spinnerPosition = adapter.getPosition(value);
            if (spinnerPosition >= 0) { // Check if the value exists in the adapter
                spinner.setSelection(spinnerPosition);
            }
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
                        expItem.setImageUrl(imageUrl);
                        Picasso.get().load(imageUrl).into(companyLogo);
                        textUploadLogo.setText("Uploaded Successfully!");
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditExp.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void displayExpDetails(ExpHelperClass expItem) {
        companyName.setText(expItem.getCompanyName());
        jobTitle.setText(expItem.getJobTitle());
        startingDate.setText(expItem.getStartingDate());
        endingDate.setText(expItem.getEndingDate());
        if (expItem.getImageUrl() != null && !expItem.getImageUrl().isEmpty()) {
            Picasso.get().load(expItem.getImageUrl()).into(companyLogo);
        }
        fetchAndSetEmploymentType(expItem.getEmploymentType());
    }

    private void saveUpdatedExperience() {
        try {
            String updatedCompanyName = companyName.getText().toString();
            String updatedJobTitle = jobTitle.getText().toString();
            String updatedStartingDate = startingDate.getText().toString();
            String updatedEndingDate = endingDate.getText().toString();
            String updatedEmployementType = employmentType.getSelectedItem().toString();

            if (expItem != null) {
                expItem.setCompanyName(updatedCompanyName);
                expItem.setJobTitle(updatedJobTitle);
                expItem.setStartingDate(updatedStartingDate);
                expItem.setEndingDate(updatedEndingDate);
                expItem.setEmploymentType(updatedEmployementType);

                ExpReference.child(expItem.getExpId()).setValue(expItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditExp.this, "Experience updated successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after updating
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update experience", e);
                            Toast.makeText(EditExp.this, "Failed to update experience: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e(TAG, "expItem is null during save");
                Toast.makeText(this, "Error: Experience item is null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveUpdatedExperience: Error updating experience", e);
            Toast.makeText(this, "Error updating experience: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this experience item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExperienceItem();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteExperienceItem() {
        if (expItem != null && expItem.getExpId() != null) {
            ExpReference.child(expItem.getExpId()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditExp.this, "experience item deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after deletion
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditExp.this, "Failed to delete experience item", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Invalid experience item", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startingDate.setText(String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth)+"-"+String.valueOf(year));
            }
        }, 2022, 0, 15);
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
                // Set the endingDate TextView to "Present"
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
                    // Set the endingDate TextView to the selected date
                    endingDate.setText((monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });
        }
    }
}