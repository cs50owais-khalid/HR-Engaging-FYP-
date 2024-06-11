package com.example.hrengaging;

import android.widget.TextView;

import java.io.Serializable;

public class ExpHelperClass implements Serializable {

    private String expId, userId, CompanyName, JobTitle, employmentType, startingDate, endingDate,imageUrl;


    public ExpHelperClass(String expId,String userId, String CompanyName, String JobTitle, String employmentType, String startingDate, String endingDate)
    {

        this.expId = expId;
        this.userId = userId;
        this.CompanyName = CompanyName;
        this.JobTitle = JobTitle;
        this.employmentType = employmentType;
        this.startingDate = startingDate;
        this.endingDate = endingDate;

    }

    public ExpHelperClass() {
    }


    public String getExpId() {
        return expId;
    }

    public void setExpId(String expId) {
        this.expId = expId;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }


    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String CompanyName) {
        this.CompanyName = CompanyName;
    }

    public String getJobTitle() {
        return JobTitle;
    }
    public void setJobTitle(String JobTitle) {
        this.JobTitle = JobTitle;
    }
    public String getEmploymentType() {
        return employmentType;
    }
    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getStartingDate(){
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }
}
