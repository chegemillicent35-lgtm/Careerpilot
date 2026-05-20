package com.example.careerpilot;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CounselorDashboardActivity extends AppCompatActivity {

    private ListView lvAppointments;
    private ArrayList<String> appointmentList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_dashboard);

        lvAppointments = findViewById(R.id.lv_appointments);
        appointmentList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        lvAppointments.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    String date = appointmentSnapshot.child("date").getValue(String.class);
                    String time = appointmentSnapshot.child("time").getValue(String.class);
                    String status = appointmentSnapshot.child("status").getValue(String.class);
                    String appointmentInfo = "Date: " + date + "\nTime: " + time + "\nStatus: " + status;
                    appointmentList.add(appointmentInfo);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
