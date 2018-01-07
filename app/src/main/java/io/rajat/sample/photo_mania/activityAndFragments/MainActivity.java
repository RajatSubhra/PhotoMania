package io.rajat.sample.photo_mania.activityAndFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import io.rajat.sample.photo_mania.R;

public class MainActivity extends AppCompatActivity {


    static boolean isLogin;


    public void setLoginFlag(boolean val){
        isLogin = val;
    }
    public boolean getLoginFlag(){
        return  isLogin;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //executeDelayed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SignUpFragment mainFragment = new SignUpFragment();

        fragmentTransaction.replace(R.id.mainlayout, mainFragment).commit();

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainlayout);
        if (fragment instanceof DetailImageFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainlayout, new TabFragment()).commit();

        }else {
            super.onBackPressed();
        }
    }

//    private void executeDelayed() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // execute after 500ms
//                hideNavBar();
//            }
//        }, 500);
//    }


//    private void hideNavBar() {
//        if (Build.VERSION.SDK_INT >= 19) {
//            View v = getWindow().getDecorView();
//            v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }


}
