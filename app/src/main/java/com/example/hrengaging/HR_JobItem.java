package com.example.hrengaging;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;




public class HR_JobItem implements Serializable {

    private String jobId;
    private String jobTitle;
    private String companyName;
    private String employmentType;
    private String salary;
    private String seniorityLevel;
    private String jobDescription;
    private String imageUrl;

    private String city;
    private Button details;

    // Add a default constructor (required for Firebase)
    public HR_JobItem() {
    }

    public HR_JobItem(String jobId,String jobTitle, String companyName,String employmentType,String salary,String city,String seniorityLevel, String jobDescription, String imageUrl) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.employmentType = employmentType;
        this.salary = salary;
        this.seniorityLevel = seniorityLevel;
        this.jobDescription = jobDescription;
        this.city = city;
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
    public String getCity() {
        return city;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}




