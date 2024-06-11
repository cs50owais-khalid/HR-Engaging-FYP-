package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SaveJobs extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView savedJobsListView;
    private List<JobItem> savedJobsList;
    private JobAdapter savedJobsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_jobs);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.jobs);
        savedJobsListView = findViewById(R.id.saveJobs);
        savedJobsList = new ArrayList<>();
        savedJobsAdapter = new JobAdapter(this, savedJobsList, true);
        savedJobsListView.setAdapter(savedJobsAdapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.jobs) {
                return true;
            } else if (item.getItemId() == R.id.notification) {
                startActivity(new Intent(getApplicationContext(), Notify.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("savedJobs");
        // Retrieve and display saved jobs
        displaySavedJobs();
    }

    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }

    private void displaySavedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Query savedJobsQuery = databaseReference.child(userId);
            savedJobsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    savedJobsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        JobItem jobItem = snapshot.getValue(JobItem.class);
                        if (jobItem != null) {
                            savedJobsList.add(jobItem);
                        }
                    }
                    savedJobsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error retrieving saved jobs: " + databaseError.getMessage());
                }
            });
        }
    }

    // Add this method to remove unsaved jobs from the local list
    public void removeUnsavedJob(JobItem jobItem) {
        savedJobsList.remove(jobItem);
        savedJobsAdapter.notifyDataSetChanged();
    }


    public void updateSavedJob(JobItem jobItem, boolean isSaved) {
        jobItem.setSaved(isSaved);
        savedJobsAdapter.notifyDataSetChanged();
    }


}