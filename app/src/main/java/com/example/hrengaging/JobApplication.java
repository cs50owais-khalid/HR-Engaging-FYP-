package com.example.hrengaging;

public class JobApplication {
    private String userId;
    private String jobId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String willingTime;
    private String description;
    private String commuteDecision;
    private String documentUri;

    // Default constructor
    public JobApplication() {
        // Default constructor required for Firebase
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWillingTime() {
        return willingTime;
    }

    public void setWillingTime(String willingTime) {
        this.willingTime = willingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCommuteDecision() {
        return commuteDecision;
    }

    public void setCommuteDecision(String commuteDecision) {
        this.commuteDecision = commuteDecision;
    }

    public String getDocumentUri() {
        return documentUri;
    }

    public void setDocumentUri(String documentUri) {
        this.documentUri = documentUri;
    }
}

