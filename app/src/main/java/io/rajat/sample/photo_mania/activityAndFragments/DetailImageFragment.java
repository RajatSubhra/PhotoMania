package io.rajat.sample.photo_mania.activityAndFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.rajat.sample.photo_mania.R;
import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.dataAccessLayer.UserReadyICallBack;
import io.rajat.sample.photo_mania.models.Comment;
import io.rajat.sample.photo_mania.util.NetworkUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailImageFragment extends Fragment implements UserReadyICallBack {


    int current_image_pos;
    boolean isPossibleToDelete,isDeleted;
    private RecyclerView comments_recycler_view;
    private DetailImageFragment.VerticalProductAdapter recyclerCommentsAdapter;
    View comment_layout;
    DAO dao;
    EditText commentText;
    View likeLayout, commentLayout, deleteLayout;
    ImageButton sendCommentButton,likeButton;
    ImageView detailImage;
    TextView likeCount;
    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_detail_image, container, false);

        if(!NetworkUtility.isConnected(getContext())){
            Toasty.error(getContext(), "No Network connection! Try with stable Network Connection...", Toast.LENGTH_LONG, true).show();
            getActivity().finish();
        }


        //Retrieve the value
        Bundle bundle = getArguments();
        if(bundle!=null){
            current_image_pos = bundle.getInt("Position");
            isPossibleToDelete = bundle.getBoolean("isPossibleToDelete");
        }


        dao = DAO.getInstance();
        dao.setCallBack(this);

        detailImage = view.findViewById(R.id.imageView4);
        likeCount = view.findViewById(R.id.textViewLikeCount);
        toolbar = view.findViewById(R.id.toolbarImageDetails);
        comment_layout = view.findViewById(R.id.comment_section);
        sendCommentButton = view.findViewById(R.id.send_comment);
        commentText = view.findViewById(R.id.commenttext);
        commentLayout = view.findViewById(R.id.comment_layout);
        likeLayout = view.findViewById(R.id.like_layout);
        deleteLayout = view.findViewById(R.id.delete_layout);
        likeButton = view.findViewById(R.id.likeButton);

        if (isPossibleToDelete){
            deleteLayout.setVisibility(View.VISIBLE);
        }else{
            deleteLayout.setVisibility(View.GONE);
        }


        toolbar.setVisibility(View.GONE);

        setLikeCount();

        Glide.with(getContext())
                .load(dao.getAllImageURLforCurrentUser().get(current_image_pos))
                .placeholder(R.drawable.ic_cloud_off_red)
                .centerCrop()
                .into(detailImage);

        if (dao.isLiked(current_image_pos)) {
            likeLayout.setTag("true");
            likeButton.setImageResource(R.drawable.ic_like_fill);
        }
        else {
            likeLayout.setTag("false");
            likeButton.setImageResource(R.drawable.ic_like);
        }


        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                String value = (String) likeLayout.getTag();
                if(value.equalsIgnoreCase("false")) {
                    //liked by user
                    likeLayout.setTag("true");
                    likeButton.setImageResource(R.drawable.ic_like_fill);
                }else{
                    likeLayout.setTag("false");
                    likeButton.setImageResource(R.drawable.ic_like);
                }
                dao.likeImageForCurrentUser(current_image_pos);
            }
        });


        // Haptic Feedback for comment button
        commentLayout.setHapticFeedbackEnabled(true);
        likeLayout.setHapticFeedbackEnabled(true);
        deleteLayout.setHapticFeedbackEnabled(true);
        sendCommentButton.setHapticFeedbackEnabled(true);

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                showAlert();
            }
        });


        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform Haptic Feedback onClick
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                if(comment_layout.getVisibility()==View.VISIBLE)
                    comment_layout.setVisibility(View.GONE);
                else
                    comment_layout.setVisibility(View.VISIBLE);


            }

        });
        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                if(TextUtils.isEmpty(commentText.getText().toString())){
                    Toast.makeText(getContext(),"Nothing to comment",Toast.LENGTH_SHORT).show();
                }
                else{
                    dao.addCommentForImage(current_image_pos,commentText.getText().toString());
                    comment_layout.setVisibility(View.GONE);
                }

            }
        });



        // RecyclerView SetUp
        comments_recycler_view = view.findViewById(R.id.commentsRecyclerView);
        recyclerCommentsAdapter = new VerticalProductAdapter(dao.getAllmessageforAnImage(current_image_pos));


        LinearLayoutManager layoutmanager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        comments_recycler_view.setLayoutManager(layoutmanager);


        comments_recycler_view.setAdapter(recyclerCommentsAdapter);




        return view;
    }

    private void setLikeCount(){
        if (dao.getLikeCountForAnImage(current_image_pos)>0)
            likeCount.setText("Liked by "+String.valueOf(dao.getLikeCountForAnImage(current_image_pos))+" users");
        else{
            likeCount.setText("Be the first person to like this Image");
        }
    }

    private void showAlert(){
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle("Are you sure to delete the image?");
        adb.setIcon(android.R.drawable.ic_menu_delete);



        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dao.deleteSelectedImage(current_image_pos);
                isDeleted = true;
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainlayout, new TabFragment()).commit();

            } });


        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            } });
        adb.show();

    }

    @Override
    public void userModelUpdate() {
        if (isDeleted){
            isDeleted = false;
            return;
        }
        Log.e("CALL BACK", "userModelUpdate TRIGGERED");
        setLikeCount();
        recyclerCommentsAdapter = new VerticalProductAdapter(dao.getAllmessageforAnImage(current_image_pos));
        comments_recycler_view.setAdapter(recyclerCommentsAdapter);
        comments_recycler_view.invalidate();
    }


    // Custom Class for RecyclerView Adapter

    public class VerticalProductAdapter extends RecyclerView.Adapter<DetailImageFragment.VerticalProductAdapter.MyViewHolder> {

        private ArrayList<Comment> verticalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView messageDetails;
            public ImageView userImage;

            public MyViewHolder(View view) {
                super(view);
                messageDetails = view.findViewById(R.id.textView2);
                userImage = view.findViewById(R.id.profile_image);

            }
        }


        public VerticalProductAdapter(ArrayList<Comment> verticalList) {
            this.verticalList = verticalList;
        }

        @Override
        public DetailImageFragment.VerticalProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_item, parent, false);

            return new DetailImageFragment.VerticalProductAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int pos) {

            holder.messageDetails.setText(verticalList.get(pos).getMessage());

            //holder.userImage
            Glide.with(getContext())
                    .load(dao.getProfilePicForUser(dao.getAllImages().get(current_image_pos).getComments().get(pos).getUserID()))
                    .asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.userImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.userImage.setImageDrawable(circularBitmapDrawable);
                }
            });

        }

        @Override
        public int getItemCount() {
            return verticalList.size();
        }
    }


}
