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

public class HRJobAdapter extends ArrayAdapter<HR_JobItem> {

    public HRJobAdapter(Context context, List<HR_JobItem> HRjobItemList) {
        super(context, 0, HRjobItemList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_hr_job_item, parent, false);
        }

        final HR_JobItem currentJob = getItem(position);

        if (currentJob != null) {
            TextView jobTitleTextView = convertView.findViewById(R.id.jobTitle);
            TextView companyNameTextView = convertView.findViewById(R.id.Company);
            TextView SalaryTextView = convertView.findViewById(R.id.salary);
            ImageView CompanyImageView = convertView.findViewById(R.id.imageView);
            Button detailsButton = convertView.findViewById(R.id.detailButton);

            jobTitleTextView.setText(currentJob.getJobTitle());
            companyNameTextView.setText(currentJob.getCompanyName());
            SalaryTextView.setText(currentJob.getSalary());
            CompanyImageView.setTag(String.valueOf(currentJob.getImageUrl()));
            Picasso.get().load(currentJob.getImageUrl()).into(CompanyImageView);


            detailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start a new activity (EditJob) and pass the job details
                    Intent intent = new Intent(getContext(), EditJob.class);
                    intent.putExtra("HR_JobItem", currentJob);
                    getContext().startActivity(intent);
                }
            });
        }

        return convertView;
    }
}
