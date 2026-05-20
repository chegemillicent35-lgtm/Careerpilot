package com.example.careerpilot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private EditText emailText, passwordText;
    private String rEmail, rPassword;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            // If user is already logged in, redirect to MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views after setting the content view
        emailText = findViewById(R.id.editTextEmailAddress);
        passwordText = findViewById(R.id.editTextPassword2);
        registerTextView = findViewById(R.id.textView2);

        // Get the email and password from the intent extras
        rEmail = getIntent().getStringExtra("email");
        rPassword = getIntent().getStringExtra("password");

        // Set up the click listener for the TextView
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    public void MainActivity(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        // Validate the email and password
        if (rEmail != null && rEmail.equals(email) && rPassword != null && rPassword.equals(password)) {
            // Store login state in SharedPreferences
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_logged_in", true);
            editor.apply();

            // Redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG).show();
        }
    }
}
