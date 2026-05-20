package com.example.careerpilot;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookAppointment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        String counselor = getIntent().getStringExtra("COUNSELOR_NAME");
        ((TextView)findViewById(R.id.tvCounselorNameDisplay)).setText(counselor);

        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> {
            String name = ((EditText)findViewById(R.id.etUserName)).getText().toString();
            if (!name.isEmpty()) {
                Toast.makeText(this, "Appointment requested with " + counselor, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}