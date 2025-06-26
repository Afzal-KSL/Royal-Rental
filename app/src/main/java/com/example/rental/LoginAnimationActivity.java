package com.example.rental;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginAnimationActivity extends AppCompatActivity {

    ImageView topImage, bottomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success_animation);

        topImage = findViewById(R.id.imageTop);
        bottomImage = findViewById(R.id.imageBottom);

        // Load animations from XML
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_to_center);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_to_center);

        // Start animations
        topImage.startAnimation(topAnim);
        bottomImage.startAnimation(bottomAnim);

        // Go to Home screen after animation ends (1s + small delay)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoginAnimationActivity.this, Home.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}