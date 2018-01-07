package io.rajat.sample.photo_mania.activityAndFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;

import es.dmoral.toasty.Toasty;
import io.rajat.sample.photo_mania.R;
import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.dataAccessLayer.ICallBack;
import io.rajat.sample.photo_mania.util.NetworkUtility;


public class SignUpFragment extends Fragment implements ICallBack {


    View layout;
    Button button;
    EditText emailID,passWord,fullName,phoneNumber;
    ImageView imageView;
    TextView loginTextView;
    TextView textView,forgetPasswordTextView;
    View countrySelectionLayout;
    NiceSpinner niceSpinner;
    ProgressBar progressBar;
    DAO dao;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        layout = view.findViewById(R.id.initialFragment);
        button = view.findViewById(R.id.button);
        imageView = view.findViewById(R.id.imageView2);
        emailID = view.findViewById(R.id.editText) ;
        passWord = view.findViewById(R.id.editText2) ;
        loginTextView =  view.findViewById(R.id.loginButton);
        textView = view.findViewById(R.id.textView);
        fullName = view.findViewById(R.id.editText3);
        phoneNumber = view.findViewById(R.id.editText4);
        forgetPasswordTextView = view.findViewById(R.id.textView3);
        countrySelectionLayout = view.findViewById(R.id.selectCountry);
        niceSpinner = view.findViewById(R.id.spinner1);
        progressBar = view.findViewById(R.id.signup_progressbar);

        dao = DAO.getInstance();

        niceSpinner.attachDataSource(dao.getListOfCountries());
        niceSpinner.setBackgroundColor(Color.TRANSPARENT);

        niceSpinner.setTintColor(R.color.light_gray);
        if(dao.isLoggedin()){
            // Go to NEXT Fragment
            Fragment fragment = new TabFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainlayout, fragment);
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        }


        if(((MainActivity)getActivity()).isLogin){
            // set background color
            //layout.setBackgroundColor(Color.parseColor("#EE9F36"));
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable( getResources().getDrawable(R.drawable.login_background) );
            } else {
                layout.setBackground( getResources().getDrawable(R.drawable.login_background));
            }
            //layout.removeOnAttachStateChangeListener();

            // set button
            button.setText("Login");
            forgetPasswordTextView.setVisibility(View.VISIBLE);
            textView.setText("Don't have an account?");
            loginTextView.setText(" Sign up");

            // hide other
            fullName.setVisibility(View.GONE);
            phoneNumber.setVisibility(View.GONE);
            countrySelectionLayout.setVisibility(View.GONE);
            niceSpinner.setVisibility(View.GONE);


            //Change Photo
            imageView.setImageResource(R.drawable.loginuser);

        }


        loginTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ((MainActivity) getActivity()).setLoginFlag(!((MainActivity) getActivity()).getLoginFlag());
                        Fragment fragment = new SignUpFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.mainlayout, fragment);
                        fragmentTransaction.addToBackStack(null);

                        // Commit the transaction
                        fragmentTransaction.commit();


                    }
                }
        );


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetworkUtility.isConnected(getContext())){
                    Toasty.error(getContext(), "No Network connection!", Toast.LENGTH_LONG, true).show();
                }
                String email = emailID.getText().toString();
                String password = passWord.getText().toString();

                String fullNameStr = fullName.getText().toString();
                String phoneNumberStr = phoneNumber.getText().toString();
                String country = dao.getListOfCountries().get(niceSpinner.getSelectedIndex());
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    //Toast messsage
                    Toasty.error(getContext(), "Blank Form!", Toast.LENGTH_LONG, true).show();
                    Log.e("Invalid","Blank form");
                }
                else{

                    if (((MainActivity)getActivity()).isLogin){
                        // Login Click
                        progressBar.setVisibility(View.VISIBLE);
                        dao.loginToFirebase(email,password,SignUpFragment.this);

                    }
                    else{
                        // Sign Up Click
                        if ( TextUtils.isEmpty(fullNameStr) || TextUtils.isEmpty(phoneNumberStr)){
                            Log.e("Invalid","Blank form");
                            Toasty.error(getContext(), "Blank Form!", Toast.LENGTH_LONG, true).show();
                        }
                        progressBar.setVisibility(View.VISIBLE);
                        dao.signupToFirebase(email,password,fullNameStr,phoneNumberStr,country,SignUpFragment.this);
                    }
                }

            }
        });


        forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailID.getText().toString()!=null){
                    String userEmail = emailID.getText().toString().trim();
                    if(userEmail.equals("")){
                        Toasty.error(getContext(), "Provide your email id!", Toast.LENGTH_LONG, true).show();
                    }else{
                        dao.forgetPassword(userEmail);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void updateSuccess() {
        Log.e("TTT","Success");
        progressBar.setVisibility(View.GONE);
        // Go to NEXT Fragment
        Fragment fragment = new TabFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainlayout, fragment);

        // Commit the transaction
        fragmentTransaction.commit();

    }

    @Override
    public void updateFailure() {
        Log.e("TTT","Failure");
        Toasty.error(getContext(), "Failure!!", Toast.LENGTH_LONG, true).show();
        progressBar.setVisibility(View.GONE);
    }
}
