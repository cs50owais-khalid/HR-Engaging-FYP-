package com.example.hrengaging;

import java.io.Serializable;

public class EduHelperClass implements Serializable {

    private String eduId, userId, InstitutionName, EducationalLevel, InstitutionType, startingDate, endingDate,imageUrl;


    public EduHelperClass(String eduId, String userId, String InstitutionName, String EducationalLevel, String InstitutionType, String startingDate, String endingDate)
    {

        this.eduId = eduId;
        this.userId = userId;
        this.InstitutionName = InstitutionName;
        this.EducationalLevel = EducationalLevel;
        this.InstitutionType = InstitutionType;
        this.startingDate = startingDate;
        this.endingDate = endingDate;

    }

    public EduHelperClass() {
    }


    public String getEduId() {
        return eduId;
    }

    public void setEduId(String eduId) {
        this.eduId = eduId;
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


    public String getInstitutionName() {
        return InstitutionName;
    }

    public void setInstitutionName(String institutionName) {
        InstitutionName = institutionName;
    }

    public String getEducationalLevel() {
        return EducationalLevel;
    }

    public void setEducationalLevel(String educationalLevel) {
        EducationalLevel = educationalLevel;
    }

    public String getInstitutionType() {
        return InstitutionType;
    }

    public void setInstitutionType(String institutionType) {
        InstitutionType = institutionType;
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
