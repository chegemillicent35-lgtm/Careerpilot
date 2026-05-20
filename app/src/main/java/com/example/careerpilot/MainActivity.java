package com.example.careerpilot;

import android.content.Context;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView10).setOnClickListener(v -> startActivity(new Intent(this, takeassessment.class)));
        findViewById(R.id.textView11).setOnClickListener(v -> startActivity(new Intent(this, Searchforjobs.class)));
        findViewById(R.id.textView12).setOnClickListener(v -> startActivity(new Intent(this, Exploreoptions.class)));
        findViewById(R.id.textView7).setOnClickListener(v -> startActivity(new Intent(this, Connectcounselor.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Adds a Cart action to the toolbar
        MenuItem item = menu.add(0, 1, 0, "Cart");
        item.setIcon(android.R.drawable.ic_menu_directions).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            // RETRIEVE FROM STORAGE
            SharedPreferences sp = getSharedPreferences("CareerCart", Context.MODE_PRIVATE);
            Set<String> items = sp.getStringSet("items", new HashSet<>());

            if (items.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("My Saved Courses")
                        .setItems(items.toArray(new String[0]), null)
                        .setPositiveButton("Clear Cart", (dialog, which) -> {
                            sp.edit().remove("items").apply();
                            Toast.makeText(this, "Cart Cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Close", null)
                        .show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}