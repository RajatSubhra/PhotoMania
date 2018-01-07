package io.rajat.sample.photo_mania.models;

import java.util.ArrayList;

public class User {


    ArrayList<Photo> photos;
    String userID;
    String user_email;
    String country;
    String phoneNumber;
    String fullName;

    String profilePicURL;
    public User(String user_email, String userID, String country, String phoneNumber, String fullName) {
        this.user_email = user_email;
        this.userID = userID;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.profilePicURL = "https://firebasestorage.googleapis.com/v0/b/cloudcamera-5ab60.appspot.com/o/images%2Fusepic.png?alt=media&token=a2b39955-9ebf-4636-8570-65bef1d88d99";
        this.photos = new ArrayList<Photo>();
    }

    public User(){
        this.profilePicURL = "https://firebasestorage.googleapis.com/v0/b/cloudcamera-5ab60.appspot.com/o/images%2Fusepic.png?alt=media&token=a2b39955-9ebf-4636-8570-65bef1d88d99";
        this.photos = new ArrayList<Photo>();
    }

    public String getUser_email() {
        return user_email;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }


    public void addnewImage(Photo newImage) {
        this.photos.add(newImage);
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public String getUserID() {
        return userID;
    }
}
