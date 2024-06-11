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


public class EditCert extends AppCompatActivity {

    TextView startingDate, endingDate, textUploadLogo;
    ImageView startingCalendar, endingCalendar, InstitutionLogo, deleteBtn;
    EditText institutionName, certificationName;
    Spinner certificationLevel;
    StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DatabaseReference userCertification, userReference;
    Button btnSave;
    CertHelperClass certItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cert);

        storageReference = FirebaseStorage.getInstance().getReference("Institution_Logo");
        userReference = FirebaseDatabase.getInstance().getReference("users");
        userCertification = FirebaseDatabase.getInstance().getReference("Certification");
        institutionName = findViewById(R.id.institutionName);
        certificationName = findViewById(R.id.certificationName);
        certificationLevel = findViewById(R.id.certificationLevelSpinner);
        startingDate = findViewById(R.id.startingDate);
        endingDate = findViewById(R.id.endingDate);
        startingCalendar = findViewById(R.id.Calendar);
        endingCalendar = findViewById(R.id.endingCalendar);
        InstitutionLogo = findViewById(R.id.companyLogo);
        textUploadLogo = findViewById(R.id.textUploadLogo);
        btnSave = findViewById(R.id.btnSave);
        deleteBtn = findViewById(R.id.dltBtn);

        // Set the adapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.certification_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        certificationLevel.setAdapter(adapter);

        Intent intent = getIntent();
        certItem = (CertHelperClass) intent.getSerializableExtra("CertHelperClass");

        if (certItem != null) {
            displayCertDetails(certItem);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUpdatedCertification();
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
            Toast.makeText(this, "Error: Certification item is null", Toast.LENGTH_SHORT).show();
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
                        certItem.setImageUrl(imageUrl);
                        Picasso.get().load(imageUrl).into(InstitutionLogo);
                        textUploadLogo.setText("Uploaded Successfully!");
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditCert.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void displayCertDetails(CertHelperClass certItem) {
        institutionName.setText(certItem.getInstitutionName());
        certificationName.setText(certItem.getCertificationName());
        startingDate.setText(certItem.getStartingDate());
        endingDate.setText(certItem.getEndingDate());
        if (certItem.getImageUrl() != null && !certItem.getImageUrl().isEmpty()) {
            Picasso.get().load(certItem.getImageUrl()).into(InstitutionLogo);
        }

        fetchAndSetCertificationLevel(certItem.getCertificationalLevel());
    }

    private void fetchAndSetCertificationLevel(String CertificationLevelStr) {
        if (certItem != null) {
            userCertification.child(certItem.getCertId()).child("certificationalLevel").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String CertificationLevelValue = task.getResult().getValue(String.class);
                    if (CertificationLevelValue != null) {
                        Log.d(TAG, "Fetched Certification Level: " + CertificationLevelValue);
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) certificationLevel.getAdapter();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            Log.d(TAG, "Spinner Option " + i + ": " + adapter.getItem(i));
                        }
                        setSpinnerSelection(certificationLevel, CertificationLevelValue);
                    }
                } else {
                    Toast.makeText(EditCert.this, "Failed to fetch certification level", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "certItem is null during fetchAndSetCertificationLevel");
            Toast.makeText(this, "Error: Certification item is null", Toast.LENGTH_SHORT).show();
        }
    }


    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        if (value != null) {
            int spinnerPosition = adapter.getPosition(value);
            if (spinnerPosition >= 0) { // Check if the value exists in the adapter
                spinner.setSelection(spinnerPosition);
            } else {
                Toast.makeText(EditCert.this, "Value not found in spinner options", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveUpdatedCertification() {
        try {
            String updatedInstitutionName = institutionName.getText().toString();
            String updatedCertificationName = certificationName.getText().toString();
            String updatedStartingDate = startingDate.getText().toString();
            String updatedEndingDate = endingDate.getText().toString();
            String updatedCertificationLevel = certificationLevel.getSelectedItem().toString();

            if (certItem != null) {
                certItem.setInstitutionName(updatedInstitutionName);
                certItem.setCertificationName(updatedCertificationName);
                certItem.setStartingDate(updatedStartingDate);
                certItem.setEndingDate(updatedEndingDate);
                certItem.setCertificationalLevel(updatedCertificationLevel); // Ensure this is set correctly

                userCertification.child(certItem.getCertId()).setValue(certItem)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditCert.this, "Certification updated successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after updating
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update Certification", e);
                            Toast.makeText(EditCert.this, "Failed to update Certification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e(TAG, "certItem is null during save");
                Toast.makeText(this, "Error: Certification item is null", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveUpdatedCertification: Error updating Certification", e);
            Toast.makeText(this, "Error updating Certification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this Certification item?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCertificationItem();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCertificationItem() {
        if (certItem != null && certItem.getCertId() != null) {
            userCertification.child(certItem.getCertId()).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditCert.this, "Certification item deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity after deletion
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditCert.this, "Failed to delete Certification item", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Invalid Certification item", Toast.LENGTH_SHORT).show();
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