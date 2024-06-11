package com.example.hrengaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class CreateJob extends AppCompatActivity {

    private EditText job_title;
    private EditText job_description;
    private EditText company_name;
    private EditText salary;
    private Spinner employment_type;
    private Spinner seniority_level;
    private Button btn_post;
    private ImageView companyLogo, backBtn;
    private TextView textUploadLogo;
    private String selectedEmploymentType;
    private String selectedSeniorityLevel;

    private Spinner citySpinner;
    private String selectedCity;

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference jobsReference;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private String imageUrl;

    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isLogoUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);
        initialize(savedInstanceState);
        FirebaseApp.initializeApp(this);
    }

    private void initialize(Bundle savedInstanceState) {
        storageReference = FirebaseStorage.getInstance().getReference("job_images");
        auth = FirebaseAuth.getInstance();
        btn_post = findViewById(R.id.btnPost);
        company_name = findViewById(R.id.companyName);
        job_title = findViewById(R.id.job_title);
        job_description = findViewById(R.id.job_description);
        salary = findViewById(R.id.salary);
        employment_type = findViewById(R.id.employmentTypeSpinner);
        seniority_level = findViewById(R.id.seniorityLevelSpinner);
        companyLogo = findViewById(R.id.companyLogo);
        textUploadLogo = findViewById(R.id.textUploadLogo);
        citySpinner = findViewById(R.id.citySpinner);
        backBtn = findViewById(R.id.backtoDashboard);

        jobsReference = FirebaseDatabase.getInstance().getReference().child("jobs");

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                CreateJob();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                finish();
            }
        });
        companyLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                pickImage();
            }
        });

        // Here we are creating ArrayAdapter for employment_type
        employment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedEmploymentType = position == 0 ? "None Selected" : parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedEmploymentType = "None Selected";
            }
        });



        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.cities_array,
                android.R.layout.simple_spinner_item
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(new cityAdapter(this, android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.cities_array)));

        citySpinner.setSelection(1);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCity = position == 0 ? "-" : parentView.getItemAtPosition(position).toString();
                selectedCity = position == 1 ? "None Selected" : parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCity = "None Selected";
            }
        });


        ArrayAdapter<CharSequence> employmentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.seniority_levels,
                android.R.layout.simple_spinner_item
        );
        employmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employment_type.setAdapter(new Adapter(this, android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.employment_types)));

        employment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedEmploymentType = position == 0 ? "None Selected" : parentView.getItemAtPosition(position).toString();

                // Check if the selected employment type is the 4th option (0-4)
                if (position == 4) {
                    // If yes, disable the citySpinner
                    citySpinner.setEnabled(false);
                    citySpinner.setSelection(0); // Optionally, reset the selection
                } else {
                    // If no, enable the citySpinner
                    citySpinner.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedEmploymentType = "None Selected";
            }
        });

        // Here we are creating ArrayAdapter for seniority_level
        ArrayAdapter<CharSequence> seniorityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.seniority_levels,
                android.R.layout.simple_spinner_item
        );
        seniorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seniority_level.setAdapter(new Adapter(this, android.R.layout.simple_spinner_item, getResources().getTextArray(R.array.seniority_levels)));

        seniority_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSeniorityLevel = position == 0 ? "None Selected" : parentView.getItemAtPosition(position).toString();

                if (position == 2) {
                    // If yes, disable the salary edit box
                    salary.setEnabled(false);
                    salary.setText("-");
                } else {
                    // If no, enable the citySpinner
                    salary.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedSeniorityLevel = "None Selected";
            }
        });
    }

    @SuppressLint("NotConstructor")
    private void CreateJob() {
        // Generate a new unique JobId
        String JobId = jobsReference.push().getKey();
        String CompanyName = company_name.getText().toString();
        String JobTitle = job_title.getText().toString();
        String JobDescription = job_description.getText().toString();
        String Salary = salary.getText().toString();



        if (CompanyName.isEmpty() || JobTitle.isEmpty() || JobDescription.isEmpty() || Salary.isEmpty()) {
            Toast.makeText(CreateJob.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedEmploymentType.equals("None Selected") || selectedSeniorityLevel.equals("None Selected") || selectedCity.equals("None Selected")) {
            Toast.makeText(CreateJob.this, "Select all options", Toast.LENGTH_SHORT).show();
            return;

        } else if (!isLogoUploaded) {
            Toast.makeText(CreateJob.this, "Please upload the company logo", Toast.LENGTH_SHORT).show();
            return;
        }


        // Get the current user's ID
       // String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a job in the Realtime Database with user information
        JobhelperClass newJob = new JobhelperClass(JobId, CompanyName, JobTitle, JobDescription, Salary, selectedCity, selectedEmploymentType, selectedSeniorityLevel);
        newJob.setImageUrl(imageUrl);
        jobsReference.child(JobId).setValue(newJob).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CreateJob.this, "Job Posted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateJob.this, "Failed to Post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clear input fields after posting
        job_title.setText("");
        company_name.setText("");
        salary.setText("");
        job_description.setText("");
        employment_type.setSelection(0);
        seniority_level.setSelection(0);
        citySpinner.setSelection(0);
        imageUrl = null;


        Intent intent = new Intent(CreateJob.this,HRMainActivity.class);
        startActivity(intent);
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
            String jobId = jobsReference.push().getKey();
            uploadImage(jobId, imageUri, new ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Handle successful image upload, e.g., save the image URL
                    Toast.makeText(CreateJob.this, "Company Logo Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    // Replace the companyLogo with imageUrl
                    Picasso.get().load(imageUrl).into(companyLogo);

                    // Set the flag to indicate that the logo is uploaded
                    isLogoUploaded = true;
                    textUploadLogo.setText("Uploaded Logo Successfully!");

                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle image upload failure
                    Toast.makeText(CreateJob.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage(String jobId, Uri imageUri, ImageUploadCallback callback) {
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


    // Callback interface for handling image upload success/failure
    interface ImageUploadCallback {
        void onSuccess(String imageUrl);

        void onFailure(String errorMessage);
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
    public class cityAdapter extends ArrayAdapter<CharSequence> {
        public cityAdapter(Context context, int resource, CharSequence[] items) {
            super(context, resource, items);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);

            if (position == 0 || position == 1) {
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
