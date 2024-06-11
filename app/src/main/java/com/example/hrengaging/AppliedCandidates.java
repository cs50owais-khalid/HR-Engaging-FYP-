package com.example.hrengaging;

import android.os.Bundle;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppliedCandidates extends AppCompatActivity {
    ListView listView;
    List<helperClass> userItemList;
    DatabaseReference appliedJobsReference;
    DatabaseReference usersReference;
    AppliedCandidatesAdapter adapter;
    String jobId; // Job ID to filter candidates
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_candidates);

        backBtn = findViewById(R.id.backtoDashboard);

        // Get the job ID from the Intent
        jobId = getIntent().getStringExtra("jobId");

        listView = findViewById(R.id.appliedCandidatesList);
        userItemList = new ArrayList<>();

        FirebaseApp.initializeApp(this);
        appliedJobsReference = FirebaseDatabase.getInstance().getReference("Jobs Applied");
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        adapter = new AppliedCandidatesAdapter(this, userItemList, jobId);
        listView.setAdapter(adapter);

        fetchAppliedCandidatesForJob(jobId);

        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void fetchAppliedCandidatesForJob(String jobId) {
        appliedJobsReference.child(jobId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userItemList.clear(); // Clear the list before adding new data
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    fetchUserDetails(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppliedCandidates.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(String userId) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                helperClass user = dataSnapshot.getValue(helperClass.class);
                if (user != null) {
                    user.setUserId(userId);
                    userItemList.add(user);

                    // Sort the list by user rating in descending order
                    Collections.sort(userItemList, new Comparator<helperClass>() {
                        @Override
                        public int compare(helperClass u1, helperClass u2) {
                            return Float.compare(u2.getUserRating(), u1.getUserRating());
                        }
                    });

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AppliedCandidates.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
