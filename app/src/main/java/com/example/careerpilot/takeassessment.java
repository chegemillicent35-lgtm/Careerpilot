package com.example.careerpilot;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

public class takeassessment extends AppCompatActivity {
    private TextView statusText;
    private Button btnGetRecommendations;
    private GmsDocumentScanner scanner;
    private boolean transcriptCaptured = false;

    private final ActivityResultLauncher<IntentSenderRequest> scannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    transcriptCaptured = true;
                    statusText.setText("Status: Transcript scanned successfully!");
                    btnGetRecommendations.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeassessment);

        statusText = findViewById(R.id.statusText);
        btnGetRecommendations = findViewById(R.id.btnGetRecommendations);

        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG, GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .build();
        scanner = GmsDocumentScanning.getClient(options);

        findViewById(R.id.btnScanDoc).setOnClickListener(v -> {
            scanner.getStartScanIntent(this).addOnSuccessListener(intentSender -> {
                scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build());
            });
        });

        btnGetRecommendations.setOnClickListener(v -> promptForNumber());
    }

    private void promptForNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Phone Number");
        final EditText input = new EditText(this);
        input.setHint("07XXXXXXXX");
        builder.setView(input);
        builder.setPositiveButton("Send", (dialog, which) -> {
            String phone = input.getText().toString();
            if (phone.length() >= 10) sendSms(phone);
        });
        builder.show();
    }

    private void sendSms(String phone) {
        // Runtime Permission Check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);
            return;
        }

        String phoneFormatted = phone.startsWith("0") ? "+254" + phone.substring(1) : phone;
        String msg = "CareerPilot: We recommend Engineering. Best Unis: JKUAT, UoN, Strathmore.";

        try {
            SmsManager smsManager = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
                    this.getSystemService(SmsManager.class) : SmsManager.getDefault();
            smsManager.sendTextMessage(phoneFormatted, null, msg, null, null);
            Toast.makeText(this, "Sending SMS...", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            // FALLBACK: Open SMS App if background send is blocked
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneFormatted));
            intent.putExtra("sms_body", msg);
            startActivity(intent);
            finish();
        }
    }
}