package com.example.hrengaging;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppliedJobs extends AppCompatActivity {

    ListView listView;
    List<HR_JobItem> hr_jobItemList;
    DatabaseReference appliedJobsReference;
    DatabaseReference jobsReference;
    AppliedJobAdapter adapter;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_jobs);
        backBtn = findViewById(R.id.backtoDashboard);

        listView = findViewById(R.id.appliedjobs);
        hr_jobItemList = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        appliedJobsReference = FirebaseDatabase.getInstance().getReference("Jobs Applied");
        jobsReference = FirebaseDatabase.getInstance().getReference("jobs");

        adapter = new AppliedJobAdapter(this, hr_jobItemList);
        listView.setAdapter(adapter);

        fetchAppliedJobs();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void fetchAppliedJobs() {
        appliedJobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hr_jobItemList.clear();  // Clear the list before adding new data
                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey(); // Get the job ID
                    fetchJobDetails(jobId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppliedJobs.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchJobDetails(String jobId) {
        jobsReference.child(jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HR_JobItem jobItem = dataSnapshot.getValue(HR_JobItem.class);
                if (jobItem != null) {
                    jobItem.setJobId(jobId);
                    hr_jobItemList.add(jobItem);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppliedJobs.this, "Failed to load job details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
