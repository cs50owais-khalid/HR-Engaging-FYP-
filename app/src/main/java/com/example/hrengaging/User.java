package com.example.hrengaging;

public class User {




    public String fullName;
    public String email;


    public static User curentUser = new User();

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;

    }

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}

