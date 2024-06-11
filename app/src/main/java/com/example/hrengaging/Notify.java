package com.example.hrengaging;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Notify extends AppCompatActivity {

    private ListView listView;
    private List<NotificationJobItem> jobItems;
    private NotificationAdapter adapter;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.notification);
        // Initialize UI components
        listView = findViewById(R.id.notificationsList);
        jobItems = new ArrayList<>();
        adapter = new NotificationAdapter(this, jobItems);
        listView.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (item.getItemId() == R.id.jobs) {
                startActivity(new Intent(getApplicationContext(), SaveJobs.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (item.getItemId() == R.id.notification) {
                return true;
            } else if (item.getItemId() == R.id.profile) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        // Retrieve job data from Firebase and populate the list
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve job details from Firebase snapshot
                    String jobTitle = snapshot.child("jobTitle").getValue(String.class);
                    String companyName = snapshot.child("companyName").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String jobId = snapshot.getKey(); // Get the job ID from Firebase key
                    // Create a NotificationJobItem object and add it to the list
                    NotificationJobItem jobItem = new NotificationJobItem(jobTitle, companyName, imageUrl);
                    jobItem.setJobId(jobId);
                    jobItems.add(jobItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }
}
