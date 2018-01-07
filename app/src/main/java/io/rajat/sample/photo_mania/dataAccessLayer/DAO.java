package io.rajat.sample.photo_mania.dataAccessLayer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.rajat.sample.photo_mania.models.Comment;
import io.rajat.sample.photo_mania.models.Photo;
import io.rajat.sample.photo_mania.models.User;

public class DAO {

    FirebaseUser currentUser;
    User currentUserObject;
    private List<String> listOfCountries = new LinkedList<>(Arrays.asList("United States", "Malaysia", "Indonesia", "France", "Italy","Singapore","India","New Zealand"));


    ArrayList<User> allUsers;
    ArrayList<Photo> allImages;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    DatabaseReference myDBRef;
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    UserReadyICallBack userReadyCallBack;

    private static DAO instance = null;

    public void setCallBack(UserReadyICallBack callback)
    {
        userReadyCallBack = callback;
    }

    private DAO() {
        // Exists only to defeat instantiation.
    }
    public ArrayList<Photo> getAllImages() {
        return allImages;
    }
    public User getCurrentUserObject() {
        return currentUserObject;
    }

    private void setFireBase()
    {
        // Get all Users from DataBase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // get current DB
        database = FirebaseDatabase.getInstance();


        mAuth = FirebaseAuth.getInstance();

        // Get Current User
        currentUser = mAuth.getCurrentUser();
    }


    public static DAO getInstance()  {
        if(instance == null) {
            instance = new DAO();

            instance.setFireBase();


            //If User is alredy there
            if(instance.isLoggedin()) {
                instance.myDBRef = instance.database.getReference("users");
                instance.setCurrentUser();
            }


        }
        // Get Current User
        instance.setFireBase();

        return instance;
    }

    public boolean isLoggedin(){
        if (currentUser == null)
            return  false;
        else
            return true;
    }


    public void loginToFirebase(String email, String password, final ICallBack callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("ttt", "signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();


                            myDBRef = database.getReference("users");

                            //Get from Firebase DB and set Current User
                            setCurrentUser();

                            // call back
                            callback.updateSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("ttt", "signInWithEmail:failure", task.getException());

                            // call back
                            callback.updateFailure();
                        }

                    }
                });
    }

    private boolean checkValidEmail(String email) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        else
            return false;
    }
    private boolean checkValidPhoneNumber(String phNumber) {
        if(Patterns.PHONE.matcher(phNumber).matches())
            return true;
        else
            return false;
    }

    private boolean checkValidPassword(String password) {
        if (password.length()>=6)
            return true;
        else
            return false;
    }

    boolean formValidation(String email, String password, String phNumber){
        if(checkValidEmail(email) && checkValidPassword(password) && checkValidPhoneNumber(phNumber)){
            return true;
        }
        return false;
    }




    public void signupToFirebase(String email, String password, final String fullName, final String phNumber, final String country, final ICallBack callback) {
        if(formValidation(email, password, phNumber)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("ttt", "createUserWithEmail:success");
                                currentUser = mAuth.getCurrentUser();

                                // add the new User
                                createUserToDB(fullName,phNumber,country);

                                //Get from Firebase DB and set Current User
                                setCurrentUser();

                                //callback
                                callback.updateSuccess();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("ttt", "createUserWithEmail:failure", task.getException());

                                // callback
                                callback.updateFailure();
                            }
                        }
                    });

        }else{
            // failed
            callback.updateFailure();
        }

    }


    public void logOut() {
        mAuth.signOut();
        currentUser = null;
        instance = null;
    }

    public static long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while(true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime+1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    public void saveProfilePicToFirebase(Bitmap bitmap, final ICallBack callBack){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();

        if(currentUser!=null){
            String pathToStore = "profile_pics/"+currentUser.getUid()+"_profile";
            StorageReference ref = mStorageRef.child(pathToStore);

            ref.putBytes(byteArray).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    callBack.updateFailure();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String profile_URL = taskSnapshot.getDownloadUrl().toString();
                    currentUserObject.setProfilePicURL(profile_URL);
                    myDBRef.child(currentUser.getUid()).child("profilePicURL").setValue(profile_URL);
                    callBack.updateSuccess();
                }
            });
        }

    }


    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap,final ICallBack callBack) {

        final Photo newImage = new Photo(String.valueOf(uniqueCurrentTimeMS()),currentUser.getUid());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();

        String pathToStore = "images/"+newImage.getImageID();
        StorageReference ref = mStorageRef.child(pathToStore);

        ref.putBytes(byteArray).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                callBack.updateFailure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                newImage.setImageURL(taskSnapshot.getDownloadUrl().toString());

                myDBRef.child(currentUser.getUid()).child("images/"+newImage.getImageID()).setValue(newImage);
                callBack.updateSuccess();
            }
        });
    }



    public void createUserToDB(String fullName, String phNumber, String country)
    {
        //get refference
        myDBRef = database.getReference("users").child(currentUser.getUid());
        User user = new User(currentUser.getEmail(),currentUser.getUid(),country,phNumber,fullName);
        myDBRef.setValue(user);
    }


    public void setCurrentUser()
    {
        //get refference
        myDBRef = database.getReference("users");

        ValueEventListener postListener = new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                allUsers = new ArrayList<>();
                allImages = new ArrayList<>();


                //getting Users one by one
                for (DataSnapshot userSnapShot: dataSnapshot.getChildren()) {
                    Log.e("SnapShot", String.valueOf(userSnapShot));


                    User user = userSnapShot.getValue(User.class);

                    Log.e("User ID",user.getUserID());


                    // Getting Images one by one
                    for (DataSnapshot imageSnapShot: dataSnapshot.child(user.getUserID()).child("images").getChildren()) {
                        Photo photo = imageSnapShot.getValue(Photo.class);
                        user.addnewImage(photo);
                        allImages.add(photo);
                    }
                    if(currentUser == null){
                        currentUser = mAuth.getCurrentUser();
                    }
                    if (user.getUserID().equalsIgnoreCase(currentUser.getUid())){
                        currentUserObject = user;
                    }


                    allUsers.add(user);



                }

               if(userReadyCallBack!=null) {
                   userReadyCallBack.userModelUpdate();
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed
            }

        };
        myDBRef.addValueEventListener(postListener);

    }

    public ArrayList<String> getAllImageURLforCurrentUser(){
        ArrayList<String> imageURLs = new ArrayList<>();
        if(currentUserObject!=null && allImages.size()>0){
            for (Photo image:allImages) {
                imageURLs.add(image.getImageURL());
            }
        }

        return imageURLs;
    }

    public void likeImageForCurrentUser(int imageNo){
        Photo image = allImages.get(imageNo);
        image.addLikes(currentUserObject.getUserID());
        myDBRef.child(image.getOwnerID()).child("images/"+image.getImageID()).setValue(image);
    }


    public void addCommentForImage(int imageNo,String message){
        Photo image =allImages.get(imageNo);
        image.addComment(message,currentUserObject.getUserID());
        myDBRef.child(image.getOwnerID()).child("images/"+image.getImageID()).setValue(image);
    }


    public ArrayList<Comment> getAllmessageforAnImage(int pos)
    {
        return allImages.get(pos).getComments();
    }

    public boolean isLiked(int imageNo){
        Photo image = allImages.get(imageNo);
        ArrayList<String> likes = image.getLikes();
        if(likes.contains(currentUserObject.getUserID())){
            return true;
        }else {
            return false;
        }
    }

    public int getLikeCountForAnImage(int imageNo){
        Photo image = allImages.get(imageNo);
        return image.getLikes().size();
    }

    public void forgetPassword(String emailID){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(emailID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void deleteSelectedImage(int pos){
        if (allImages!=null && currentUser!=null){
            Photo currentPhoto = allImages.get(pos);
            allImages.remove(pos);
            database.getReference("users").child(currentUser.getUid()).child("images")
                    .child(currentPhoto.getImageID()).setValue(null);


        }
    }

    public String getProfilePicForUser(String userID){
        if (allUsers!=null) {
            for (User user : allUsers) {
                if(user.getUserID().equalsIgnoreCase(userID)){
                    return user.getProfilePicURL();
                }
            }
            return null;
        }
        return null;
    }

    public List<String> getListOfCountries() {
        return listOfCountries;
    }
}
