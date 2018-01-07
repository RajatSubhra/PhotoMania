package io.rajat.sample.photo_mania.activityAndFragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.rajat.sample.photo_mania.R;
import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.dataAccessLayer.ICallBack;
import io.rajat.sample.photo_mania.dataAccessLayer.UserReadyICallBack;
import io.rajat.sample.photo_mania.models.Photo;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements UserReadyICallBack,ICallBack {

    DAO dao;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ImageGalleryAdapter imageGalleryAdapter;
    ProgressBar progressBar;
    boolean flag = false;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        progressBar = view.findViewById(R.id.progressBar3);
        progressBar.bringToFront();
        dao = DAO.getInstance();
        dao.setCallBack(this);

        //Recycler View
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView =  view.findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        imageGalleryAdapter = new ImageGalleryAdapter(getContext(), dao.getAllImages());
        recyclerView.setAdapter(imageGalleryAdapter);
        flag = true;
        FloatingActionButton uploadImageButton =  view.findViewById(R.id.uploadImage);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("Crop Image")
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setAspectRatio(4,3)
                        .setCropMenuCropButtonTitle("Done")
                        .setRequestedSize(400, 400,CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .setCropMenuCropButtonIcon(R.drawable.ic_done_24dp)
                        .start(getContext(),GalleryFragment.this);
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        dao.setCallBack(this);
        super.onResume();
    }

    @Override
    public void userModelUpdate() {

        Log.e("CALL BACK", "userModelUpdate TRIGGERED");
        imageGalleryAdapter = new ImageGalleryAdapter(getContext(), dao.getAllImages());
        recyclerView.setAdapter(imageGalleryAdapter);
        recyclerView.invalidate();
    }

    // it trigger when tab change
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && flag) {
            if (dao.getAllImages()!=null) {
                imageGalleryAdapter = new ImageGalleryAdapter(getContext(), dao.getAllImages());
                recyclerView.setAdapter(imageGalleryAdapter);
                recyclerView.invalidate();
            }
        }
    }

    @Override
    public void updateSuccess() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Upload Done",Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateFailure() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),"Upload Failure",Toast.LENGTH_LONG).show();
    }


    // RecyclerView
    private class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {

        @Override
        public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the layout
            View photoView = inflater.inflate(R.layout.photo_item, parent, false);

            ImageGalleryAdapter.MyViewHolder viewHolder = new ImageGalleryAdapter.MyViewHolder(photoView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position) {

            String imageString = imagesForLoad.get(position).getImageURL();

            ImageView imageView = holder.mPhotoImageView;

            if(imagesForLoad.get(position).isUserImage(dao.getCurrentUserObject().getUserID())){
                holder.isPossibleToDelete = true;
            }else{
                holder.isPossibleToDelete = false;
            }

            Glide.with(mContext)
                    .load(imageString)
                    .placeholder(R.drawable.ic_cloud_off_red)
                    .into(imageView);
        }

        @Override
        public int getItemCount() {
            if(imagesForLoad!=null)
                return (imagesForLoad.size());
            else
                return 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mPhotoImageView;
            boolean isPossibleToDelete;

            public MyViewHolder(View itemView) {

                super(itemView);
                mPhotoImageView = itemView.findViewById(R.id.iv_photo);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Fragment fragment = new DetailImageFragment();

                    Bundle args = new Bundle();
                    args.putInt("Position", position);
                    args.putBoolean("isPossibleToDelete",isPossibleToDelete);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.mainlayout, fragment);

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            }
        }

        private ArrayList<Photo> imagesForLoad;
        private Context mContext;

        public ImageGalleryAdapter(Context context, ArrayList<Photo> photos) {
            mContext = context;
            imagesForLoad = photos;
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
                    dao.encodeBitmapAndSaveToFirebase(selectedImage,GalleryFragment.this);
                    progressBar.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}


