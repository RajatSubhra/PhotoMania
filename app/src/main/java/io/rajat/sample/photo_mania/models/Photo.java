package io.rajat.sample.photo_mania.models;

import java.util.ArrayList;

/**
 * Created by Raj on 07/05/17.
 */

public class Photo {

    String imageID;
    String imageURL;
    ArrayList<String> likes;
    ArrayList<Comment> comments;
    String ownerID;

    public Photo(String id,String userID)
    {
        this.imageID = id;
        this.ownerID = userID;
        likes = new ArrayList<String>();
        comments = new ArrayList<Comment>();
    }
    public Photo(){
        likes = new ArrayList<String>();
        comments = new ArrayList<Comment>();
    }

    public boolean isUserImage(String currentUserID){
        if (currentUserID.equalsIgnoreCase(ownerID))
            return true;
        else
            return false;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageID() {
        return imageID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void addLikes(String userID){
        if(likes.contains(userID)){
            likes.remove(userID);
        }else {
            likes.add(userID);
        }
    }


    public void addComment(String message,String userID){
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setUserID(userID);
        comments.add(comment);

    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }
}
