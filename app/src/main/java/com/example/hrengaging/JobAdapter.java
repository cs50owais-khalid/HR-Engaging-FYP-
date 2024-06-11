package com.example.hrengaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends ArrayAdapter<JobItem> implements Filterable {

    private boolean isSavedJobs;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private DatabaseReference savedReference;


    private List<JobItem> jobItemListFiltered; // Filtered list of job items
    private List<JobItem> jobItemListOriginal; // Original list of job items




    public JobAdapter(Context context, List<JobItem> jobItemList) {
        super(context, 0, jobItemList);
        this.isSavedJobs = false;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("jobs");
        this.sharedPreferences = context.getSharedPreferences("Saved jobs", Context.MODE_PRIVATE);
        this.savedReference = FirebaseDatabase.getInstance().getReference("savedJobs");



        this.jobItemListFiltered = new ArrayList<>(jobItemList);
        this.jobItemListOriginal = new ArrayList<>(jobItemList);
    }

    public JobAdapter(Context context, List<JobItem> jobItemList, boolean isSavedJobs) {
        super(context, 0, jobItemList);
        this.isSavedJobs = isSavedJobs;
        if (isSavedJobs) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            this.databaseReference = FirebaseDatabase.getInstance().getReference("savedJobs").child(userId);
        } else {
            this.databaseReference = FirebaseDatabase.getInstance().getReference("jobs");
        }
        this.sharedPreferences = context.getSharedPreferences("Saved jobs", Context.MODE_PRIVATE);
        this.savedReference = FirebaseDatabase.getInstance().getReference("savedJobs");

        this.jobItemListFiltered = new ArrayList<>(jobItemList);
        this.jobItemListOriginal = new ArrayList<>(jobItemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.job_items, parent, false);
        }

        final JobItem currentJob = getItem(position);

        if (currentJob != null) {
            TextView jobTitleTextView = convertView.findViewById(R.id.textView);
            TextView companyNameTextView = convertView.findViewById(R.id.textView2);
            TextView salaryTextView = convertView.findViewById(R.id.textView3);
            ImageView companyImageView = convertView.findViewById(R.id.imageView);
            Button detailsButton = convertView.findViewById(R.id.detailbutton);
            saveButton = convertView.findViewById(R.id.saveBtn);

            jobTitleTextView.setText(currentJob.getJobTitle());
            companyNameTextView.setText(currentJob.getCompanyName());
            salaryTextView.setText(currentJob.getSalary());
            companyImageView.setTag(String.valueOf(currentJob.getImageUrl()));
            Picasso.get().load(currentJob.getImageUrl()).into(companyImageView);

            boolean isSavedLocally = sharedPreferences.getBoolean(currentJob.getJobId(), false);
            saveButton.setBackgroundResource(currentJob.isSaved() ? R.drawable.save : R.drawable.unsave);

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), JobDetails.class);
                    intent.putExtra("JobItem", currentJob);
                    getContext().startActivity(intent);
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSaved = !isSavedLocally;
                    currentJob.setSaved(isSaved);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(currentJob.getJobId(), isSaved);
                    editor.apply();
                    notifyDataSetChanged();

                    if (isSaved) {
                        saveJob(currentJob);
                    } else {
                        unsaveJob(currentJob);
                    }
                }
            });
        }

        return convertView;
    }


    private void saveJob(JobItem jobItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        jobItem.setUserId(userId);

        DatabaseReference savedJobsReference = FirebaseDatabase.getInstance().getReference("savedJobs").child(userId).child(jobItem.getJobId());

        savedJobsReference.setValue(jobItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Job saved successfully", Toast.LENGTH_SHORT).show();
                        jobItem.setSaved(true);
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to save job: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Failed to save job", e);
                    }
                });
    }


    private void unsaveJob(JobItem jobItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference savedJobsReference = FirebaseDatabase.getInstance().getReference("savedJobs").child(userId).child(jobItem.getJobId());

        savedJobsReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Job unsaved successfully", Toast.LENGTH_SHORT).show();
                        jobItem.setSaved(false);
                        ((SaveJobs) getContext()).removeUnsavedJob(jobItem);
                        notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to unsaved job: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Failed to unsaved job", e);
                    }
                });
    }
}
