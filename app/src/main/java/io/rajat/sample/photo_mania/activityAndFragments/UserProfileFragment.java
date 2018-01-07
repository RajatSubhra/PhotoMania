package io.rajat.sample.photo_mania.activityAndFragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.rajat.sample.photo_mania.R;
import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.dataAccessLayer.ICallBack;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment implements ICallBack {

    DAO dao;
    TextView user_full_name,user_image_count,user_country,user_email,user_phone_number;
    ImageView profile_image;
    Button update_profile_pic;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        dao = DAO.getInstance();

        user_full_name = view.findViewById(R.id.user_full_name);
        user_image_count = view.findViewById(R.id.user_image_count);
        user_country = view.findViewById(R.id.user_country);;
        user_email = view.findViewById(R.id.user_email);
        user_phone_number = view.findViewById(R.id.user_phone_number);
        profile_image = view.findViewById(R.id.user_profile_image);
        update_profile_pic = view.findViewById(R.id.change_profile_pic);
        progressBar = view.findViewById(R.id.progressBar2);

        return  view;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setCurrentProfile();
        }

    }


    private void setCurrentProfile(){
        dao = DAO.getInstance();
        if (dao.getCurrentUserObject()!=null){
            user_full_name.setText(dao.getCurrentUserObject().getFullName());
            user_image_count.setText(String.valueOf(dao.getCurrentUserObject().getPhotos().size()));
            user_country.setText(dao.getCurrentUserObject().getCountry());
            user_email.setText(dao.getCurrentUserObject().getUser_email());
            user_phone_number.setText(dao.getCurrentUserObject().getPhoneNumber());

            Glide.with(getContext()).load(dao.getCurrentUserObject().getProfilePicURL())
                    .asBitmap().centerCrop().into(new BitmapImageViewTarget(profile_image) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profile_image.setImageDrawable(circularBitmapDrawable);
                }
            });


            // if you update to Glide v4 then -
            // Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(imageView);


            update_profile_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setActivityTitle("Crop Image")
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setFixAspectRatio(true)
                            .setAspectRatio(1,1)
                            .setCropMenuCropButtonTitle("Done")
                            .setRequestedSize(400, 400,CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                            .setCropMenuCropButtonIcon(R.drawable.ic_done_24dp)
                            .start(getContext(),UserProfileFragment.this);
                }
            });

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
                    dao.saveProfilePicToFirebase(selectedImage,UserProfileFragment.this);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.bringToFront();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("CROP ERR",error.getLocalizedMessage());
            }
        }
    }

    @Override
    public void updateSuccess() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Upload Done",Toast.LENGTH_LONG).show();
        setCurrentProfile();
    }

    @Override
    public void updateFailure() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Upload Failure",Toast.LENGTH_LONG).show();
    }
}
