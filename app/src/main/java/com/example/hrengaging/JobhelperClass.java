package com.example.hrengaging;

public class JobhelperClass {

    private String ImageUrl, JobId, CompanyName, JobTitle, JobDescription, Salary, city, employmentType, seniorityLevel;


    public JobhelperClass(String JobId, String CompanyName, String JobTitle, String JobDescription, String Salary,String city, String employmentType, String seniorityLevel) {

        this.JobId = JobId;
        this.CompanyName = CompanyName;
        this.JobTitle = JobTitle;
        this.JobDescription = JobDescription;
        this.Salary = Salary;
        this.city=city;
        this.employmentType = employmentType;
        this.seniorityLevel = seniorityLevel;

    }

    public JobhelperClass() {
    }




    public String getJobId(){
        return JobId;
    }

    public void setJobId(String JobId){
        this.JobId = JobId;
    }

    public String getImageUrl(){
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl){
        this.ImageUrl = ImageUrl;
    }

//    public String getUserId(){
//        return userId;
//    }
//    public void setUserId(String userId){
//        this.userId = userId;
//    }


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

    public String getJobDescription() {
        return JobDescription;
    }

    public void setJobDescription(String JobDescription) {
        this.JobDescription = JobDescription;
    }

    public String getSalary() {
        return Salary;
    }

    public void setSalary(String Salary) {
        this.Salary = Salary;
    }


    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(String seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
