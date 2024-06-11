package com.example.hrengaging;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class ExpAdapter extends ArrayAdapter<ExpHelperClass> {

    private Context mContext;
    private List<ExpHelperClass> mExpList;

    public ExpAdapter(Context context, List<ExpHelperClass> expList) {
        super(context, 0, expList);
        mContext = context;
        mExpList = expList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.exp_dashboard_items, parent, false);
        }

        ExpHelperClass currentItem = mExpList.get(position);

        // Initialize views
        ImageView companyLogo = listItem.findViewById(R.id.companyLogo);
        TextView companyName = listItem.findViewById(R.id.companyName);
        TextView positionText = listItem.findViewById(R.id.position);
        TextView durationFrom = listItem.findViewById(R.id.durationFrom);
        TextView durationTo = listItem.findViewById(R.id.durationTo);
        ImageView detailButton = listItem.findViewById(R.id.detailButton);

        // Set data to views
        Picasso.get().load(currentItem.getImageUrl()).into(companyLogo); // Change this to your data source

        companyName.setText(currentItem.getCompanyName());
        positionText.setText(currentItem.getJobTitle());
        durationFrom.setText(currentItem.getStartingDate());
        durationTo.setText(currentItem.getEndingDate());


        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditExp.class);
                intent.putExtra("ExpHelperClass", currentItem);
                getContext().startActivity(intent);
            }
        });

        return listItem;



    }



}
