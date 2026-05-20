package com.example.careerpilot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000; // 3000 milliseconds (3 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to start the LoginActivity after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the LoginActivity
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                finish(); // Close the SplashActivity
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
