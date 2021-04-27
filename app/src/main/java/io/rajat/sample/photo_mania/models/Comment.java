package io.rajat.sample.photo_mania.models;

/**
 * Created by Rajat on 5/11/17.
 */

public class Comment {

    String message;
    String userID;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}

