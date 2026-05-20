package com.example.careerpilot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class Exploreoptions extends AppCompatActivity {

    private Spinner spinnerCourse;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploreoptions);

        spinnerCourse = findViewById(R.id.spinner_course);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // Sample Data
        String[] courses = {"B.Sc. Computer Science", "Medicine", "Engineering", "Law"};
        spinnerCourse.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses));

        btnAddToCart.setOnClickListener(v -> {
            String selected = spinnerCourse.getSelectedItem().toString();

            // DIRECT SAVE TO STORAGE (Replacing CartManager)
            SharedPreferences sp = getSharedPreferences("CareerCart", Context.MODE_PRIVATE);
            Set<String> currentCart = new HashSet<>(sp.getStringSet("items", new HashSet<>()));
            currentCart.add(selected);
            sp.edit().putStringSet("items", currentCart).apply();

            Toast.makeText(this, selected + " added to Cart!", Toast.LENGTH_SHORT).show();
        });
    }
}