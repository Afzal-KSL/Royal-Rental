package com.example.rental;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class splash extends AppCompatActivity {
    Animation topAnim;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim);

        imageView = findViewById(R.id.imageView);
        imageView.setAnimation(topAnim);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(splash.this, MainActivity.class));
            finish(); // Close SplashActivity
        }, 3000);
    }
}