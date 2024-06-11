package com.example.hrengaging;

public class helperClass {
    private String userId, fullname, email, password, confirmpassword, imageUrl, city;
    private float userRating;

    // Constructor with imageUrl
    public helperClass(String userId, String fullname, String email, String password, String confirmpassword, String imageUrl, float userRating, String city) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.imageUrl = imageUrl;
        this.userRating = userRating;
        this.city = city;
    }

    // Constructor without imageUrl but with userRating and city
    public helperClass(String userId, String fullname, String email, String password, String confirmpassword, float userRating, String city) {
        this(userId, fullname, email, password, confirmpassword, null, userRating, city);
    }

    // Constructor without imageUrl, userRating, and city
    public helperClass(String userId, String fullname, String email, String password, String confirmpassword) {
        this(userId, fullname, email, password, confirmpassword, null, 0.0f, null);
    }

    // Empty constructor
    public helperClass() {}

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
