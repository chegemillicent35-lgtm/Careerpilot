package com.example.careerpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailText = findViewById(R.id.editTextEmailAddress);
        passwordText = findViewById(R.id.editTextPassword2);
        TextView btn=findViewById(R.id.textView6);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                startActivity(new Intent(Register.this,Login.class));
            }
        });
    }

    public void Register(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (!isValidPassword(password))
        {
            Toast.makeText(this, "Password does not match rules", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("email",email);
        intent.putExtra("password",password);
        startActivity(intent);
    }
    Pattern lowercase = Pattern.compile(".*[a-z].*");
    Pattern uppercase = Pattern.compile(".*[A-Z].*");
    Pattern number = Pattern.compile(".*[0-9].*");
    Pattern specialChar = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    private boolean isValidPassword(String password)
    {
        if(password.length()<8)
        {
            return false;
        }
        if(!lowercase.matcher(password).matches())
        {
            return false;
        }
        if(!uppercase.matcher(password).matches())
        {
            return false;
        }
        if(!number.matcher(password).matches())
        {
            return false;
        }
        if(!specialChar.matcher(password).matches())
        {
            return false;
        }
        return true;

    }
}


