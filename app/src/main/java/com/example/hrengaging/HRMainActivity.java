package com.example.hrengaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HRMainActivity extends AppCompatActivity {
    private ListView listView2;
    private List<HR_JobItem> hr_jobItemList;

    private Button create;
    ImageView drawerButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private DatabaseReference jobsRef;
    private DatabaseReference hrRef;
    private FirebaseAuth firebaseAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hr_activity_main);

        listView2 = findViewById(R.id.listView2);
        hr_jobItemList = new ArrayList<>();
        create = findViewById(R.id.btnCreateJob);
        drawerButton = findViewById(R.id.imageView3);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
            hrRef = FirebaseDatabase.getInstance().getReference("HR").child(userID);
        } else {
            // Handle the case where the user is not logged in
            // For example, redirect to login activity
            startActivity(new Intent(HRMainActivity.this, MultiLogin.class));
            finish();
            return;
        }

        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        // here we creating and setting the adapter
        final HRJobAdapter hrjobAdapter = new HRJobAdapter(this, hr_jobItemList);
        listView2.setAdapter(hrjobAdapter);

        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        hrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Fetch user's image and name from database
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("fullname").getValue(String.class);

                    // Set user's name and image in the sidebar header
                    TextView txtviewName = findViewById(R.id.txtviewName);
                    txtviewName.setText(userName);
                    CircleImageView userImage = findViewById(R.id.userImage);
                    Picasso.get().load(R.drawable.hr_logo).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Create = new Intent(HRMainActivity.this, CreateJob.class);
                startActivity(Create);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.profilesetting) {
                    Intent intent = new Intent(HRMainActivity.this, HREditProfile.class);
                    startActivity(intent);
                }
                if (itemId == R.id.appliedCandidates) {
                    Intent intent = new Intent(HRMainActivity.this, AppliedJobs.class);
                    startActivity(intent);
                }
                if (itemId == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HRMainActivity.this, MultiLogin.class);
                    startActivity(intent);
                    finish();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    return true;
                }

                drawerLayout.close();

                return false;
            }
        });


        // here we are trying to fetch job data from Firebase and putting our data in job adapter
        jobsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HR_JobItem hr_jobItem = snapshot.getValue(HR_JobItem.class);
                hr_jobItemList.add(hr_jobItem);
                hrjobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HR_JobItem updatedJobItem = snapshot.getValue(HR_JobItem.class);

                // Find the position of the updated job in the list
                int position = findPositionInList(updatedJobItem.getJobId());

                if (position != -1) {
                    // Update the item in the list
                    hr_jobItemList.set(position, updatedJobItem);

                    // Notify the adapter that the data has changed
                    hrjobAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                HR_JobItem removedJobItem = snapshot.getValue(HR_JobItem.class);

                // Find the position of the removed job in the list
                int position = findPositionInList(removedJobItem.getJobId());

                if (position != -1) {
                    // Remove the item from the list
                    hr_jobItemList.remove(position);

                    // Notify the adapter that the item has been removed
                    hrjobAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    private int findPositionInList(String jobId) {
        // Find the position of the job in the list based on its ID
        for (int i = 0; i < hr_jobItemList.size(); i++) {
            if (hr_jobItemList.get(i).getJobId().equals(jobId)) {
                return i;
            }
        }
        return -1;
    }
}