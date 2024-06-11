package com.example.hrengaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlinx.coroutines.Job;

public class EditJob extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView companyNameTextView;
    private TextView jobTitleTextView;
    private Spinner employment_type;
    private TextView salaryTextView;
    private Spinner seniority_level;
    private TextView job_description;
    private Spinner citySpinner;

    private ImageView deleteIcon, backBtn;

    private String selectedEmploymentType;
    private String selectedSeniorityLevel;
    private String selectedCity;

    private Button save;

    private DatabaseReference jobsReference;

    private HR_JobItem hr_jobItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        jobsReference = FirebaseDatabase.getInstance().getReference().child("jobs");

        Intent intent = getIntent();
        hr_jobItem = (HR_JobItem) intent.getSerializableExtra("HR_JobItem");

        // Initialize UI elements
        //profileImageView = findViewById(R.id.profileImageView);
        companyNameTextView = findViewById(R.id.companyName);
        jobTitleTextView = findViewById(R.id.job_title);
        employment_type = findViewById(R.id.employmentTypeSpinner);
        salaryTextView = findViewById(R.id.salary);
        seniority_level = findViewById(R.id.seniorityLevelSpinner);
        job_description = findViewById(R.id.job_description);
        citySpinner = findViewById(R.id.citySpinner);
        deleteIcon = findViewById(R.id.delete);
        save = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.backtoDashboard);
        profileImageView = findViewById(R.id.companyLogo);

        String[] employmentArray = {"None Selected", "Full-Time", "Part-Time", "Contract","Remote"};
        String[] seniorityArray = {"None Selected", "Paid Internship", "Unpaid Internship", "Entry Level","Junior","Mid-Level","Senior","Lead","Manager","Director","Executive"};
        String[] cityArray = getResources().getStringArray(R.array.cities_array);


        ArrayAdapter<String> employmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employmentArray);
        employment_type.setAdapter(employmentAdapter);

        ArrayAdapter<String> seniorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, seniorityArray);
        seniority_level.setAdapter(seniorityAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityArray);
        citySpinner.setAdapter(cityAdapter);


        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        employment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {


                    // Optionally, reset the selection to a default value
                    employment_type.setSelection(1);
                } else {
                    // Continue with your existing logic
                    selectedEmploymentType = parentView.getItemAtPosition(position).toString();

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedEmploymentType = "None Selected";
            }
        });

        final String currentJobId = hr_jobItem.getJobId();

        jobsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final String _childKey = snapshot.getKey();
                final HashMap<String, Object> _childValue = snapshot.getValue(_ind);
                jobsReference.child(currentJobId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String companyLogoUrl = snapshot.child("imageUrl").getValue(String.class);
                            if (!TextUtils.isEmpty(companyLogoUrl)) {
                                Picasso.get().load(companyLogoUrl).into(profileImageView);
                            }
                            String companyName = snapshot.child("companyName").getValue(String.class);
                            String jobTitle = snapshot.child("jobTitle").getValue(String.class);
                            String salary = snapshot.child("salary").getValue(String.class);
                            String jobDescription = snapshot.child("jobDescription").getValue(String.class);
                            String employmentType = snapshot.child("employmentType").getValue(String.class);
                            String seniorityLevel = snapshot.child("seniorityLevel").getValue(String.class);
                            String city = snapshot.child("city").getValue(String.class);

                            companyNameTextView.setText(companyName);
                            jobTitleTextView.setText(jobTitle);
                            salaryTextView.setText(salary);
                            job_description.setText(jobDescription);

                            int position = employmentAdapter.getPosition(employmentType);
                            employment_type.setSelection(position);

                            int position2 = seniorityAdapter.getPosition(seniorityLevel);
                            seniority_level.setSelection(position2);

                            int position3 = cityAdapter.getPosition(city);
                            citySpinner.setSelection(position3);



                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Get updated values from UI elements
                                    String updatedCompanyName = companyNameTextView.getText().toString();
                                    String updatedJobTitle = jobTitleTextView.getText().toString();
                                    String updatedSalary = salaryTextView.getText().toString();
                                    String updatedJobDescription = job_description.getText().toString();
                                    String updatedEmploymentType = employment_type.getSelectedItem().toString();
                                    String updatedSeniorityLevel = seniority_level.getSelectedItem().toString();
                                    String updatedCity = cityAdapter.getItem(citySpinner.getSelectedItemPosition());

                                    if (TextUtils.isEmpty(updatedCompanyName) || TextUtils.isEmpty(updatedJobTitle) ||
                                            TextUtils.isEmpty(updatedSalary) || TextUtils.isEmpty(updatedJobDescription) ||
                                            "None Selected".equals(updatedEmploymentType) || "None Selected".equals(updatedSeniorityLevel)) {

                                        // Show an error message to the user
                                        Toast.makeText(EditJob.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Update values in Firebase
                                        jobsReference.child(currentJobId).child("companyName").setValue(updatedCompanyName);
                                        jobsReference.child(currentJobId).child("jobTitle").setValue(updatedJobTitle);
                                        jobsReference.child(currentJobId).child("salary").setValue(updatedSalary);
                                        jobsReference.child(currentJobId).child("jobDescription").setValue(updatedJobDescription);
                                        jobsReference.child(currentJobId).child("employmentType").setValue(updatedEmploymentType);
                                        jobsReference.child(currentJobId).child("seniorityLevel").setValue(updatedSeniorityLevel);
                                        jobsReference.child(currentJobId).child("city").setValue(updatedCity);

                                        // Inform the user about the successful update
                                        Toast.makeText(EditJob.this, "Job details updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Job");
        builder.setMessage("Are you sure you want to delete this job?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteJob();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteJob() {
        final String currentJobId = hr_jobItem.getJobId();

        jobsReference.child(currentJobId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Job deleted successfully
                        Toast.makeText(EditJob.this, "Job deleted successfully", Toast.LENGTH_SHORT).show();
                        // Finish the activity or navigate to another activity if needed
                        finish();
                        Intent intent = new Intent(EditJob.this,HRMainActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete job
                        Toast.makeText(EditJob.this, "Failed to delete job", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
