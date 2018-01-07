package io.rajat.sample.photo_mania.activityAndFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import io.rajat.sample.photo_mania.R;

public class Splash extends AppCompatActivity {

    TextView textView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView  = (TextView)findViewById(R.id.splash_title);
        imageView = (ImageView)findViewById(R.id.splash_image);
        Animation splashAnim = AnimationUtils.loadAnimation(this,R.anim.splash_transition);
        textView.startAnimation(splashAnim);
        imageView.startAnimation(splashAnim);
        final Intent mainScreen = new Intent(Splash.this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(2800);
                    startActivity(mainScreen);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}
