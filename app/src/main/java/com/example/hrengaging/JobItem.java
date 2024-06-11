package com.example.hrengaging;



import android.widget.Button;
import java.io.Serializable;


public class JobItem implements Serializable {
    private String jobTitle;
    private String companyName;
    private String employmentType;
    private String salary;
    private String seniorityLevel;
    private String jobDescription;
    private String imageUrl;
    private Button details;
    private boolean saved;
    private String jobId;
    private String userId;

    // Add a default constructor (required for Firebase)
    public JobItem() {
    }

    public JobItem(String jobTitle, String companyName, String employmentType, String salary, String seniorityLevel, String jobDescription, String imageUrl) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.employmentType = employmentType;
        this.salary = salary;
        this.seniorityLevel = seniorityLevel;
        this.jobDescription = jobDescription;
        this.imageUrl = imageUrl;
    }




    public String getJobTitle() {
        return jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getSalary() {
        return salary;
    }

    public String getSeniorityLevel() {
        return seniorityLevel;
    }

    public String getJobDescription() {
        return jobDescription;
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

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
