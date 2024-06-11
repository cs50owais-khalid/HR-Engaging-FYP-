package com.example.hrengaging;

import java.io.Serializable;

public class CertHelperClass implements Serializable {

    private String CertId, userId, InstitutionName, certificationName, CertificationalLevel, startingDate, endingDate,imageUrl;


    public CertHelperClass(String CertId, String userId, String InstitutionName, String certificationName,String CertificationalLevel, String startingDate, String endingDate)
    {

        this.CertId = CertId;
        this.userId = userId;
        this.InstitutionName = InstitutionName;
        this.certificationName = certificationName;
        this.CertificationalLevel = CertificationalLevel;
        this.startingDate = startingDate;
        this.endingDate = endingDate;

    }

    public CertHelperClass() {
    }


    public String getCertId() {
        return CertId;
    }

    public void setCertId(String CertId) {
        this.CertId = CertId;
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

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getCertificationalLevel() {
        return CertificationalLevel;
    }

    public void setCertificationalLevel(String CertificationalLevel) {
        this.CertificationalLevel = CertificationalLevel;
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