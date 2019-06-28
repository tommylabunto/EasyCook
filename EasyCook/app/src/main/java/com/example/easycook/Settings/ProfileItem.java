package com.example.easycook.Settings;

// user data on firestore collection ("users")
public class ProfileItem {

    // unique identifier from firebaseuser (also set to document id)
    private String uid;
    private String username;
    private String email;
    private int phoneNumber;

    // empty constructor
    public ProfileItem() {
    }

    public ProfileItem(String uid, String username, String email, int phoneNumber) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
