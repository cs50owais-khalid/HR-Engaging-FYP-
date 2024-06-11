package com.example.hrengaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationJobItem> {

    private Context mContext;
    private List<NotificationJobItem> mJobItems;

    public NotificationAdapter(Context context, List<NotificationJobItem> jobItems) {
        super(context, 0, jobItems);
        mContext = context;
        mJobItems = jobItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(R.layout.notification_jobitem, parent, false);
        }

        final NotificationJobItem currentJobItem = mJobItems.get(position);

        TextView jobTitleTextView = listItemView.findViewById(R.id.textView);
        TextView companyNameTextView = listItemView.findViewById(R.id.textView2);
        ImageView companyImageView = listItemView.findViewById(R.id.imageView);
        CardView cardView = listItemView.findViewById(R.id.cardView);

        jobTitleTextView.setText(currentJobItem.getJobTitle());
        companyNameTextView.setText(currentJobItem.getCompanyName());
        // Load image using Picasso library
        Picasso.get().load(currentJobItem.getImageUrl()).into(companyImageView);

        // Set OnClickListener to the card view
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), JobDetails.class);
//                intent.putExtra("JobItem", currentJobItem);
//                getContext().startActivity(intent);
//            }
//        });

        return listItemView;
    }
}
