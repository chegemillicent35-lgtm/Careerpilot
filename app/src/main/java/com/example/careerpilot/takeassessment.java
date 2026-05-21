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
import androidx.annotation.NonNull;
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
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;

    private TextView statusText;
    private Button btnGetRecommendations;
    private GmsDocumentScanner scanner;
    private boolean transcriptCaptured = false;
    private String pendingPhoneNumber;

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
            scanner.getStartScanIntent(this)
                    .addOnSuccessListener(intentSender ->
                            scannerLauncher.launch(new IntentSenderRequest.Builder(intentSender).build()))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Could not open scanner: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
            String phone = input.getText().toString().trim();
            if (!transcriptCaptured) {
                Toast.makeText(this, "Scan a transcript first.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() >= 10) {
                sendSms(phone);
            } else {
                Toast.makeText(this, "Enter a valid phone number.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void sendSms(String phone) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            pendingPhoneNumber = phone;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
            return;
        }

        pendingPhoneNumber = null;
        String phoneFormatted = phone.startsWith("0") ? "+254" + phone.substring(1) : phone;
        String msg = "CareerPilot: We recommend Engineering. Best Unis: JKUAT, UoN, Strathmore.";

        try {
            SmsManager smsManager = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
                    this.getSystemService(SmsManager.class) : SmsManager.getDefault();
            smsManager.sendTextMessage(phoneFormatted, null, msg, null, null);
            Toast.makeText(this, "Recommendations sent by SMS.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneFormatted));
            intent.putExtra("sms_body", msg);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != SMS_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingPhoneNumber != null) {
            sendSms(pendingPhoneNumber);
        } else {
            pendingPhoneNumber = null;
            Toast.makeText(this, "SMS permission is required to send recommendations.", Toast.LENGTH_LONG).show();
        }
    }
}
