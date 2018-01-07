package io.rajat.sample.photo_mania.activityAndFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment{

    ImageButton signUpButton;
    TextView loginTextView;
    private  Fragment fragment;
    DAO dao;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main, container, false);

        signUpButton = (ImageButton)view.findViewById(R.id.signupButton);
        loginTextView = (TextView) view.findViewById(R.id.loginButton);

        dao = DAO.getInstance();
        if(dao.isLoggedin()){
            fragment = new SignUpFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainlayout, fragment);
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        }

        signUpButton.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        signUpButton.setImageResource(R.drawable.signuppress);
                        return false;
                    }
                }
        );



        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        //islogin = false;
                        ((MainActivity) getActivity()).setLoginFlag(false);
                        fragment = new SignUpFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainlayout, fragment);
                        fragmentTransaction.addToBackStack(null);

                        // Commit the transaction
                        fragmentTransaction.commit();

                    }
                }
        );


        loginTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //islogin = true;
                        ((MainActivity) getActivity()).setLoginFlag(true);
                        fragment = new SignUpFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainlayout, fragment);
                        fragmentTransaction.addToBackStack(null);

                        // Commit the transaction
                        fragmentTransaction.commit();


                    }
                }
        );

        return view;

    }

}
