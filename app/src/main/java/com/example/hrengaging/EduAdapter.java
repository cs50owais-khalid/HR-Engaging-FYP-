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
import java.util.List;

public class EduAdapter extends ArrayAdapter<EduHelperClass> {

    private Context mContext;
    private List<EduHelperClass> mEduList;

    public EduAdapter(Context context, List<EduHelperClass> eduList) {
        super(context, 0, eduList);
        mContext = context;
        mEduList = eduList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.exp_dashboard_items, parent, false);
        }

        EduHelperClass currentItem = mEduList.get(position);

        // Initialize views
        ImageView institutionLogo = listItem.findViewById(R.id.companyLogo);
        TextView institutionName = listItem.findViewById(R.id.companyName);
        TextView institutionType = listItem.findViewById(R.id.position);
        TextView instDurationFrom = listItem.findViewById(R.id.durationFrom);
        TextView instDurationTo = listItem.findViewById(R.id.durationTo);
        ImageView detailButton = listItem.findViewById(R.id.detailButton);

        // Set data
        Picasso.get().load(currentItem.getImageUrl()).into(institutionLogo);
        institutionName.setText(currentItem.getInstitutionName());
        institutionType.setText(currentItem.getInstitutionType());
        instDurationFrom.setText(currentItem.getStartingDate());
        instDurationTo.setText(currentItem.getEndingDate());


        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditEdu.class);
                intent.putExtra("EduHelperClass", currentItem);
                getContext().startActivity(intent);
            }
        });

        return listItem;
    }
}
