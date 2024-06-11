package com.example.hrengaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class AppliedCandidatesAdapter extends ArrayAdapter<helperClass> {
    private String jobId;
    private Context mContext;
    private float userRating;

    public AppliedCandidatesAdapter(Context context, List<helperClass> userItemList, String jobId) {
        super(context, 0, userItemList);
        this.jobId = jobId;
        this.mContext = context;
        this.userRating = userRating;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_user_item, parent, false);
        }

        final helperClass currentUser = getItem(position);

        if (currentUser != null) {
            TextView userNameTextView = convertView.findViewById(R.id.userName);
            ImageView userImageView = convertView.findViewById(R.id.imageView);
//            RatingBar ratingBar = convertView.findViewById(R.id.userRating);
            TextView location = convertView.findViewById(R.id.location);
            Button detailsButton = convertView.findViewById(R.id.detailButton);

            userNameTextView.setText(currentUser.getFullname());
            String Location = currentUser.getCity();
            if (Location == null || Location.isEmpty()) {
                location.setText("no location");
            } else {
                location.setText(Location);
            }


            // Set the rating from the extra
//            ratingBar.setRating(userRating);

            Picasso.get()
                    .load(currentUser.getImageUrl())
                    .placeholder(R.drawable.userprofileicon) // Default placeholder image
                    .error(R.drawable.userprofileicon)       // Default error image
                    .into(userImageView);

            detailsButton.setOnClickListener(v -> {
                // Handle button click
                Intent intent = new Intent(getContext(), ApplicationScreeningDashboard.class);
                intent.putExtra("UserId", currentUser.getUserId());
                intent.putExtra("jobId", jobId);
                getContext().startActivity(intent);
            });
        }

        return convertView;
    }
}
