package io.rajat.sample.photo_mania.activityAndFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import io.rajat.sample.photo_mania.R;
import io.rajat.sample.photo_mania.dataAccessLayer.DAO;
import io.rajat.sample.photo_mania.util.NetworkUtility;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    DAO dao;

    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarImageDetails);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //toolbar.setTitle("");

        // Hide Title Of the toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolBarTitle = view.findViewById(R.id.toolbar_title1); ////"UserName"+"'s "+"Home"
        ImageButton logoutButton =  view.findViewById(R.id.logoutButton);

        if(!NetworkUtility.isConnected(getContext())){
            showNoInternetAlert();
        }



        dao = DAO.getInstance();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.logOut();
                // Go to Main Fragment
                Fragment fragment = new SignUpFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainlayout, fragment);
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });




        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        TabLayout.Tab galleryTab = tabLayout.newTab().setText("Gallery");
        TabLayout.Tab profileTab = tabLayout.newTab().setText("User Profile");

        // Set Icons for tab
        galleryTab.setIcon(R.drawable.gallery);
        profileTab.setIcon(R.drawable.user_icon);

        tabLayout.addTab(galleryTab);
        tabLayout.addTab(profileTab);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager =  view.findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }


    void showNoInternetAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Network Unavailable!!");
        builder.setMessage("Error getting data from the Internet\nTry with stable Network Connection!");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                getActivity().finish();
                dialog.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
