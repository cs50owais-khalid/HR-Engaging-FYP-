package com.example.hrengaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAppliedJobAdapter extends ArrayAdapter<JobItem> {

    public UserAppliedJobAdapter(Context context, List<JobItem> jobItemList) {
        super(context, 0, jobItemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_hr_job_item, parent, false);
        }

        final JobItem currentJob = getItem(position);

        if (currentJob != null) {
            TextView jobTitleTextView = convertView.findViewById(R.id.jobTitle);
            TextView companyNameTextView = convertView.findViewById(R.id.Company);
            TextView salaryTextView = convertView.findViewById(R.id.salary);
            ImageView companyImageView = convertView.findViewById(R.id.imageView);
            Button detailsButton = convertView.findViewById(R.id.detailButton);

            jobTitleTextView.setText(currentJob.getJobTitle());
            companyNameTextView.setText(currentJob.getCompanyName());
            salaryTextView.setText(currentJob.getSalary());
            Picasso.get().load(currentJob.getImageUrl()).into(companyImageView);

            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), JobDetails.class);
                    intent.putExtra("JobItem", currentJob);
                    getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
