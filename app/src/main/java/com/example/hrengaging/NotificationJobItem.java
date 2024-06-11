package com.example.hrengaging;



import android.widget.Button;
import java.io.Serializable;


public class NotificationJobItem implements Serializable {
    private String jobTitle;
    private String companyName;
    private String imageUrl;
    private String jobId;
    private String userId;

    // Add a default constructor (required for Firebase)
    public NotificationJobItem() {
    }

    public NotificationJobItem(String jobTitle, String companyName, String imageUrl) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.imageUrl = imageUrl;
    }




    public String getJobTitle() {
        return jobTitle;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
