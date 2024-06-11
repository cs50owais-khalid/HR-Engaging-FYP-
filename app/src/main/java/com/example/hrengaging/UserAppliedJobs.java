package com.example.hrengaging;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAppliedJobs extends AppCompatActivity {

    ListView listView;
    List<JobItem> jobItemList;
    DatabaseReference userAppliedJobsReference;
    DatabaseReference jobsReference;
    UserAppliedJobAdapter adapter;
    ImageView backBtn;
    private FirebaseAuth firebaseAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_jobs);
        backBtn = findViewById(R.id.backtoDashboard);

        listView = findViewById(R.id.appliedjobs);
        jobItemList = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        userAppliedJobsReference = FirebaseDatabase.getInstance().getReference("Jobs Applied");
        jobsReference = FirebaseDatabase.getInstance().getReference("jobs");

        adapter = new UserAppliedJobAdapter(this, jobItemList);
        listView.setAdapter(adapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchUserAppliedJobs();
    }

    private void fetchUserAppliedJobs() {
        userAppliedJobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobItemList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                        String jobId = jobSnapshot.getKey();
                        if (jobSnapshot.child(userID).exists()) {
                            fetchJobDetails(jobId);
                        }
                    }
                } else {
                    Toast.makeText(UserAppliedJobs.this, "No applied jobs found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserAppliedJobs.this, "Failed to fetch applied jobs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchJobDetails(String jobId) {
        jobsReference.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                JobItem jobItem = dataSnapshot.getValue(JobItem.class);
                if (jobItem != null) {
                    jobItemList.add(jobItem);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserAppliedJobs.this, "Failed to fetch job details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
