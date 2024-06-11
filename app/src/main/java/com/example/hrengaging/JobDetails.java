package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class JobDetails extends AppCompatActivity {

    private CircleImageView companyLogo;
    private TextView companyNameTextView;
    private TextView jobTitleTextView;
    private TextView jobTypeTextView;
    private TextView salaryTextView;
    private TextView positionTextView;
    private TextView requirementsTextView;
    private Button apply;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference jobsAppliedReference;
    private String currentUserId;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        jobsAppliedReference = FirebaseDatabase.getInstance().getReference("Jobs Applied");

        Intent intent = getIntent();
        JobItem jobItem = (JobItem) intent.getSerializableExtra("JobItem");
        jobId = jobItem.getJobId();

        // Initialize UI elements
        companyLogo = findViewById(R.id.companyLogo);
        companyNameTextView = findViewById(R.id.companyNameTextView);
        jobTitleTextView = findViewById(R.id.jobTitleTextView);
        jobTypeTextView = findViewById(R.id.jobTypeTextView);
        salaryTextView = findViewById(R.id.salaryTextView);
        positionTextView = findViewById(R.id.positionTextView);
        requirementsTextView = findViewById(R.id.requirements);
        apply = findViewById(R.id.btnApply);

        // Load image into CircleImageView using Picasso
        Picasso.get().load(jobItem.getImageUrl()).into(companyLogo);

        // Update UI with job details
        companyNameTextView.setText(jobItem.getCompanyName());
        jobTitleTextView.setText(jobItem.getJobTitle());
        jobTypeTextView.setText(jobItem.getEmploymentType());
        salaryTextView.setText(jobItem.getSalary());
        positionTextView.setText(jobItem.getSeniorityLevel());
        requirementsTextView.setText(jobItem.getJobDescription());

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfAlreadyApplied();
            }
        });
    }

    private void checkIfAlreadyApplied() {
        jobsAppliedReference.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUserId)) {
                    Toast.makeText(JobDetails.this, "You have already applied for this job.", Toast.LENGTH_SHORT).show();
                } else {
                    // Navigate to ApplyJob activity if not already applied
                    Intent applyIntent = new Intent(JobDetails.this, ApplyJob.class);
                    applyIntent.putExtra("JobId", jobId);
                    startActivity(applyIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JobDetails.this, "Failed to check application status.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
