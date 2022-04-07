package com.example.myjourneyjournal;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView = findViewById(R.id.imgLogo);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.animation);
        imageView.startAnimation(animation);

        Thread thread=new Thread(() -> {
            try {
                sleep(3000);
                startActivity(SignIn.getIntent(getApplicationContext()));
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}