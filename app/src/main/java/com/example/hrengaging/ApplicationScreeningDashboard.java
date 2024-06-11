package com.example.hrengaging;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplicationScreeningDashboard extends AppCompatActivity {

    TextView userName, UserObjective;
    ListView expList, eduList, certList;
    CircleImageView userImage;
    ImageView candidateDts, backBtn;
    CardView objectiveCardView;
    List<ExpHelperClass> experienceItemList = new ArrayList<>();
    List<EduHelperClass> educationItemList = new ArrayList<>();
    List<CertHelperClass> certificationItemList = new ArrayList<>();
    DatabaseReference userExperience;
    DatabaseReference userEducation;
    DatabaseReference userCertification;

    ExpAdapter expAdapter;
    EduAdapter eduAdapter;
    CertAdapter certAdapter;
    private RatingBar ratingBar;
    String imageUrl;

    private static final int MAX_RATING = 5;
    private static final int EDUCATION_WEIGHT = 1;
    private static final int EXPERIENCE_WEIGHT = 1;
    private static final int CERTIFICATION_WEIGHT = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_screening_dashboard);

        // Initialize UI elements
        userName = findViewById(R.id.userNameTextView);
        ratingBar = findViewById(R.id.ratingBar);
        userImage = findViewById(R.id.userImage);
        UserObjective = findViewById(R.id.userObjective);
        objectiveCardView = findViewById(R.id.objectiveCardView);
        expList = findViewById(R.id.expListView);
        eduList = findViewById(R.id.eduListView);
        certList = findViewById(R.id.CertListView);
        candidateDts = findViewById(R.id.candidateDetails);
        backBtn = findViewById(R.id.backtoDashboard);

        userExperience = _firebase.getReference("Experience");
        userEducation = _firebase.getReference("Education");
        userCertification = _firebase.getReference("Certification");

        // Get the user ID from the intent or from FirebaseAuth
        final String userID = getIntent().getStringExtra("UserId");
        final String jobId = getIntent().getStringExtra("jobId");

        // Check if userID and jobId are not null
        if (userID == null || jobId == null) {
            Toast.makeText(this, "Job ID or User ID is missing", Toast.LENGTH_SHORT).show();
            Log.e("ApplicationScreeningDashboard", "Job ID or User ID is missing");
            return;
        }

        expAdapter = new ExpAdapter(this, experienceItemList);
        eduAdapter = new EduAdapter(this, educationItemList);
        certAdapter = new CertAdapter(this, certificationItemList);

        expList.setAdapter(expAdapter);
        eduList.setAdapter(eduAdapter);
        certList.setAdapter(certAdapter);

        firebaseAuth = FirebaseAuth.getInstance();

        Toast.makeText(ApplicationScreeningDashboard.this, "UserID: " + userID, Toast.LENGTH_SHORT).show();

        // Load profile image
        loadProfileImage(userID);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        candidateDts.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicationScreeningDashboard.this, CandidateDetails.class);
            intent.putExtra("jobId", jobId);
            intent.putExtra("userId", userID);
            startActivity(intent);
        });

        // Load user info
        loadUserInfo(userID);

        // Load user experience and education data
        loadUserExperience(userID);
        loadUserEducation(userID);
        loadUserCertifications(userID);

        calculateAndUpdateRating(userID);
    }

    private void loadProfileImage(String userID) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userID + ".*");

        imageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getMetadata().addOnSuccessListener(metadata -> {
                    String contentType = metadata.getContentType();
                    if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(userImage))
                                .addOnFailureListener(e -> Toast.makeText(ApplicationScreeningDashboard.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(ApplicationScreeningDashboard.this, "Failed to load profile picture", Toast.LENGTH_SHORT).show());
    }


    private void loadUserInfo(String userID) {
        DatabaseReference usersRef = _firebase.getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap<String, Object> userMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (userMap != null && userID.equals(userMap.get("userId"))) {
                        String fullName = (String) userMap.get("fullname");
                        String imageUrl = (String) userMap.get("imageUrl");
                        String objective = (String) userMap.get("Objective");

                        if (fullName != null) {
                            userName.setText(fullName);
                            UserObjective.setText(objective);
                        }
                        if (imageUrl != null) {
                            Picasso.get().load(imageUrl).into(userImage);
                            ApplicationScreeningDashboard.this.imageUrl = imageUrl;
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });

        UserObjective.post(() -> {
            int objectiveHeight = UserObjective.getHeight();

            // Set the height of the CardView dynamically
            ViewGroup.LayoutParams layoutParams = objectiveCardView.getLayoutParams();
            layoutParams.height = objectiveHeight;
            objectiveCardView.setLayoutParams(layoutParams);
        });
    }


    private void loadUserExperience(String userID) {
        userExperience.orderByChild("userId").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                experienceItemList.clear();
                for (DataSnapshot expSnapshot : dataSnapshot.getChildren()) {
                    ExpHelperClass exp = expSnapshot.getValue(ExpHelperClass.class);
                    if (exp != null) {
                        experienceItemList.add(exp);
                    }
                }
                expAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadUserEducation(String userID) {
        userEducation.orderByChild("userId").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                educationItemList.clear();
                for (DataSnapshot eduSnapshot : dataSnapshot.getChildren()) {
                    EduHelperClass edu = eduSnapshot.getValue(EduHelperClass.class);
                    if (edu != null) {
                        educationItemList.add(edu);
                    }
                }
                eduAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void loadUserCertifications(String userID) {
        userCertification.orderByChild("userId").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                certificationItemList.clear();
                for (DataSnapshot certSnapshot : dataSnapshot.getChildren()) {
                    CertHelperClass cert = certSnapshot.getValue(CertHelperClass.class);
                    if (cert != null) {
                        certificationItemList.add(cert);
                    }
                }
                certAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }


    private void calculateAndUpdateRating(String userID) {
        final int[] educationCount = {0};
        final int[] experienceCount = {0};
        final int[] certificationCount = {0};
        final boolean[] isEducationLoaded = {false};
        final boolean[] isExperienceLoaded = {false};
        final boolean[] isCertificationLoaded = {false};

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getRef().equals(userExperience)) {
                    experienceCount[0] = (int) dataSnapshot.getChildrenCount();
                    isExperienceLoaded[0] = true;
                } else if (dataSnapshot.getRef().equals(userEducation)) {
                    educationCount[0] = (int) dataSnapshot.getChildrenCount();
                    isEducationLoaded[0] = true;
                } else if (dataSnapshot.getRef().equals(userCertification)) {
                    certificationCount[0] = (int) dataSnapshot.getChildrenCount();
                    isCertificationLoaded[0] = true;
                }

                if (isEducationLoaded[0] && isExperienceLoaded[0] && isCertificationLoaded[0]) {
                    updateRating(educationCount[0], experienceCount[0], certificationCount[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        };

        userExperience.orderByChild("userId").equalTo(userID).addListenerForSingleValueEvent(listener);
        userEducation.orderByChild("userId").equalTo(userID).addListenerForSingleValueEvent(listener);
        userCertification.orderByChild("userId").equalTo(userID).addListenerForSingleValueEvent(listener);
    }

    private void updateRating(int educationCount, int experienceCount, int certificationCount) {
        // Each education entry contributes 0.5 to the rating
        float educationContribution = educationCount * 0.5f;

        // Each certification entry contributes 1 to the rating
        float certificationContribution = certificationCount * 1.0f;

        // Calculate total months of experience
        float totalExperienceMonths = 0.0f;
        for (ExpHelperClass exp : experienceItemList) {
            totalExperienceMonths += calculateExperienceMonths(exp);
        }

        // Each 12 months of experience adds 1 to the rating (assuming 1 year = 1 rating point)
        float experienceContribution = totalExperienceMonths / 12.0f;

        // Sum the contributions to get the total rating
        float rating = educationContribution + experienceContribution + certificationContribution;

        // Cap the rating to the maximum allowed rating
        rating = Math.min(rating, MAX_RATING);

        // Set rating bar
        ratingBar.setRating(rating);

        // Display a toast message with the calculated rating
        Toast.makeText(ApplicationScreeningDashboard.this, "Calculated Rating: " + rating, Toast.LENGTH_SHORT).show();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e("ApplicationScreeningDashboard", "Current user is null when updating rating");
            return;
        }
        DatabaseReference userRef = _firebase.getReference("users").child(currentUser.getUid());
        userRef.child("rating").setValue(rating).addOnSuccessListener(aVoid -> {
            // Successfully saved rating
            Toast.makeText(ApplicationScreeningDashboard.this, "Rating updated in the database", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Failed to save rating
            Toast.makeText(ApplicationScreeningDashboard.this, "Failed to update rating in the database", Toast.LENGTH_SHORT).show();
        });
    }

    private float calculateExperienceMonths(ExpHelperClass exp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date startDate = sdf.parse(exp.getStartingDate());
            Date endDate = sdf.parse(exp.getEndingDate());
            if (startDate != null && endDate != null) {
                long diffInMillies = endDate.getTime() - startDate.getTime();
                return (float) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 30.0f;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0.0f;
    }
}
