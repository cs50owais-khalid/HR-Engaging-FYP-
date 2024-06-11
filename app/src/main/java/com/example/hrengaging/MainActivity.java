package com.example.hrengaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private ImageView savedJobs;
    private DrawerLayout drawerLayout;
    private ImageView buttonDrawer ;
    private NavigationView navigationView;
    private ListView listView;
    private List<JobItem> jobItemList;

    private List<JobItem> savedJobsList;

    private List<JobItem> finalJobsList;

    private DatabaseReference jobsRef;
    private DatabaseReference savedJobsRef;

    private EditText searchView;

    JobAdapter jobAdapter;


    @SuppressLint({"NonConstantResourceId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        savedJobs = findViewById(R.id.saveJobs);
        listView = findViewById(R.id.listView);
        drawerLayout = findViewById(R.id.drawer_layout);
        buttonDrawer = findViewById(R.id.logo);
        navigationView = findViewById(R.id.navigationView);
        searchView = findViewById(R.id.searchView);
        jobItemList = new ArrayList<>();
        savedJobsList = new ArrayList<>();
        finalJobsList = new ArrayList<>();


        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        savedJobsRef = FirebaseDatabase.getInstance().getReference("savedJobs");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();





        if (user == null) {
            Intent intent = new Intent(MainActivity.this, MultiLogin.class);
            startActivity(intent);
            finish();
        } else if (user != null) {
            String userID = user.getUid();
//            Toast.makeText(MainActivity.this, "User ID: " + userID, Toast.LENGTH_SHORT).show();




            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Fetch user's image and name from database
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("fullname").getValue(String.class);
                        String userImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                        // Set user's name and image in the sidebar header
                        TextView txtviewName = findViewById(R.id.txtviewName);
                        CircleImageView userImage = findViewById(R.id.userImage);
                        txtviewName.setText(userName);
                        // Use Glide or any other library to load the image
                        if (userImageUrl != null && !userImageUrl.isEmpty()) {
                            Picasso.get().load(userImageUrl).placeholder(R.drawable.userprofileicon).into(userImage);
                        } else {
                            // If no image found, set default image from drawable
                            userImage.setImageResource(R.drawable.userprofileicon);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            bottomNavigationView.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.home) {
                    return true;
                } else if (item.getItemId() == R.id.jobs) {
                    startActivity(new Intent(getApplicationContext(), SaveJobs.class));
                    overrideActivityTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
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


            buttonDrawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.open();
                }
            });




            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.profilesetting) {
                        Intent intent = new Intent(MainActivity.this, EditProfile.class);
                        startActivity(intent);
                    }
                    if (itemId == R.id.jobsApplied) {
                        Intent intent = new Intent(MainActivity.this, UserAppliedJobs.class);
                        startActivity(intent);}
                    if (itemId == R.id.logout) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, MultiLogin.class);
                        startActivity(intent);
                        finish();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        return true;
                    }

                    drawerLayout.close();

                    return false;
                }
            });

            jobAdapter = new JobAdapter(this, jobItemList);
            listView.setAdapter(jobAdapter);

            loadJobs();
            loadSavedJobs();
            finalJobs();


            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = s.toString();
                    search(searchText);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

    }


    private void overrideActivityTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim);
    }

    public void search(String query){
        List<JobItem> TempJobList = new ArrayList<>();

        if(query.isEmpty()){
            TempJobList = jobItemList;
        }else{
            for (JobItem item : jobItemList) {
                if (item.getJobTitle().toLowerCase().contains(query)) {
                    TempJobList.add(item);
                    Log.d("Filtered Job", item.getJobTitle() + " added to filtered list");
                }
            }
        }
        jobAdapter = new JobAdapter(this, TempJobList);
        listView.setAdapter(jobAdapter);
        jobAdapter.notifyDataSetChanged();
    }

    private void loadSavedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Query savedJobsQuery = savedJobsRef.child(userId);
            savedJobsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    savedJobsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        JobItem jobItem = snapshot.getValue(JobItem.class);
                        if (jobItem != null) {
                            savedJobsList.add(jobItem);
                        }
                    }

                    jobAdapter.notifyDataSetChanged();

                    if (!jobItemList.isEmpty() && !savedJobsList.isEmpty()) {
                        finalJobs();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error retrieving saved jobs: " + databaseError.getMessage());
                }
            });
        }
    }

    private void loadJobs(){
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                JobItem jobItem = null;
                String jobId = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    jobItem = snapshot.getValue(JobItem.class);
                    jobId = snapshot.getKey();
                    jobItemList.add(jobItem);
                }

                jobAdapter.notifyDataSetChanged();

                finalJobs();

                sendNotification("New Job Posted", jobItem.getJobTitle(), jobId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void finalJobs(){

        for (JobItem job : jobItemList) {
            for (JobItem savedJob : savedJobsList) {
                if (savedJob.getJobId().equals(job.getJobId())) {
                    job.setSaved(true);
                }
            }
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.job_title);
            String description = getString(R.string.job_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String title, String message, String jobId) {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the user has already been notified for this job
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        notificationsRef.orderByChild("userId_jobId").equalTo(userId + "_" + jobId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User hasn't been notified for this job, send notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "channel_id")
                            .setSmallIcon(R.drawable.notifications)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                    notificationManager.notify(0, builder.build());

                    // Save notification to Firebase
                    DatabaseReference newNotificationRef = notificationsRef.push();
                    newNotificationRef.child("userId_jobId").setValue(userId + "_" + jobId);
                    newNotificationRef.child("message").setValue(title + ": " + message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }


}

