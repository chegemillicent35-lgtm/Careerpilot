package com.example.careerpilot;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentFilter;
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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class takeassessment extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private static final String SMS_SENT_ACTION = "com.example.careerpilot.SMS_SENT";

    private TextView statusText;
    private Button btnGetRecommendations;
    private GmsDocumentScanner scanner;
    private boolean transcriptCaptured = false;
    private String pendingPhoneNumber;
    private String recommendationMessage;

    private final BroadcastReceiver smsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == RESULT_OK) {
                Toast.makeText(context, "Recommendations were sent to the phone network.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(context, "SMS was not sent. Opening SMS app instead.", Toast.LENGTH_LONG).show();
                openSmsApp(intent.getStringExtra("phone"), recommendationMessage);
            }
        }
    };

    private final ActivityResultLauncher<IntentSenderRequest> scannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    GmsDocumentScanningResult scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.getData());
                    analyzeTranscript(scanResult);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeassessment);

        statusText = findViewById(R.id.statusText);
        btnGetRecommendations = findViewById(R.id.btnGetRecommendations);
        registerSmsSentReceiver();

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

    private void analyzeTranscript(GmsDocumentScanningResult scanResult) {
        btnGetRecommendations.setVisibility(View.GONE);
        transcriptCaptured = false;
        recommendationMessage = null;
        statusText.setText("Status: Transcript scanned. Analyzing grades...");

        if (scanResult == null || scanResult.getPages() == null || scanResult.getPages().isEmpty()) {
            statusText.setText("Status: No readable transcript image found. Please scan again.");
            return;
        }

        readTranscriptPages(scanResult.getPages(), 0, new StringBuilder());
    }

    private void readTranscriptPages(List<GmsDocumentScanningResult.Page> pages, int pageIndex, StringBuilder transcriptText) {
        if (pageIndex >= pages.size()) {
            recommendationMessage = buildRecommendations(transcriptText.toString());
            transcriptCaptured = true;
            statusText.setText("Status: Transcript analyzed. Recommendations are ready to send.");
            btnGetRecommendations.setVisibility(View.VISIBLE);
            return;
        }

        InputImage image;
        try {
            image = InputImage.fromFilePath(this, pages.get(pageIndex).getImageUri());
        } catch (IOException e) {
            statusText.setText("Status: Could not read scanned page. Please scan again.");
            return;
        }

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    transcriptText.append('\n').append(text.getText());
                    recognizer.close();
                    readTranscriptPages(pages, pageIndex + 1, transcriptText);
                })
                .addOnFailureListener(e -> {
                    recognizer.close();
                    statusText.setText("Status: Could not analyze transcript text. Please scan a clearer image.");
                });
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
                Toast.makeText(this, "Scan and analyze a transcript first.", Toast.LENGTH_SHORT).show();
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
        String msg = recommendationMessage;
        if (msg == null || msg.trim().isEmpty()) {
            Toast.makeText(this, "No recommendations are ready yet. Please scan again.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager smsManager = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
                    this.getSystemService(SmsManager.class) : SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            for (int i = 0; i < parts.size(); i++) {
                Intent sentIntent = new Intent(SMS_SENT_ACTION);
                sentIntent.putExtra("phone", phoneFormatted);
                sentIntents.add(PendingIntent.getBroadcast(
                        this,
                        i,
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
            }
            smsManager.sendMultipartTextMessage(phoneFormatted, null, parts, sentIntents, null);
            Toast.makeText(this, "Sending analyzed course recommendations...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            openSmsApp(phoneFormatted, msg);
        }
    }

    private String buildRecommendations(String transcriptText) {
        String text = transcriptText == null ? "" : transcriptText.toUpperCase(Locale.US);
        int math = gradePoint(text, "MATHEMATICS", "MATHS", "MATH");
        int english = gradePoint(text, "ENGLISH", "ENG");
        int kiswahili = gradePoint(text, "KISWAHILI", "KISW");
        int biology = gradePoint(text, "BIOLOGY", "BIO");
        int chemistry = gradePoint(text, "CHEMISTRY", "CHEM");
        int physics = gradePoint(text, "PHYSICS", "PHY");
        int business = gradePoint(text, "BUSINESS", "COMMERCE");
        int computer = gradePoint(text, "COMPUTER", "COMPUTER STUDIES", "ICT");
        int geography = gradePoint(text, "GEOGRAPHY", "GEO");
        int history = gradePoint(text, "HISTORY", "CRE", "RELIGIOUS");
        int agriculture = gradePoint(text, "AGRICULTURE", "AGRI");

        ArrayList<String> courses = new ArrayList<>();
        if (math >= 9 && physics >= 8) {
            courses.add("Engineering");
        }
        if (math >= 8 && (computer >= 7 || physics >= 7)) {
            courses.add("Computer Science / Software Engineering");
        }
        if (biology >= 9 && chemistry >= 9 && (math >= 7 || physics >= 7)) {
            courses.add("Medicine, Nursing or Pharmacy");
        }
        if (business >= 7 && math >= 6) {
            courses.add("Business, Accounting or Finance");
        }
        if (english >= 7 && (history >= 7 || kiswahili >= 7)) {
            courses.add("Law, Education or Communication");
        }
        if (geography >= 7 && math >= 6) {
            courses.add("Architecture, GIS or Urban Planning");
        }
        if (agriculture >= 7 || (biology >= 7 && chemistry >= 6)) {
            courses.add("Agriculture, Environmental Science or Food Science");
        }
        if (courses.isEmpty()) {
            courses.add("Diploma or certificate pathways matching your strongest subjects");
            courses.add("Career counseling for a detailed placement review");
        }

        return "CareerPilot transcript analysis: Based on the scanned grades, you appear eligible for "
                + joinCourses(courses)
                + ". Please confirm with official university entry requirements.";
    }

    private int gradePoint(String text, String... subjects) {
        for (String subject : subjects) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(subject) + "\\b[^A-Z0-9]{0,20}(A-|A|B\\+|B-|B|C\\+|C-|C|D\\+|D-|D|E)\\b");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return gradeToPoint(matcher.group(1));
            }
        }
        return 0;
    }

    private int gradeToPoint(String grade) {
        switch (grade) {
            case "A": return 12;
            case "A-": return 11;
            case "B+": return 10;
            case "B": return 9;
            case "B-": return 8;
            case "C+": return 7;
            case "C": return 6;
            case "C-": return 5;
            case "D+": return 4;
            case "D": return 3;
            case "D-": return 2;
            case "E": return 1;
            default: return 0;
        }
    }

    private String joinCourses(ArrayList<String> courses) {
        if (courses.size() == 1) {
            return courses.get(0);
        }

        StringBuilder joined = new StringBuilder();
        for (int i = 0; i < courses.size(); i++) {
            if (i > 0) {
                joined.append(i == courses.size() - 1 ? " and " : ", ");
            }
            joined.append(courses.get(i));
        }
        return joined.toString();
    }

    private void openSmsApp(String phone, String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
        intent.putExtra("sms_body", msg);
        startActivity(intent);
    }

    private void registerSmsSentReceiver() {
        IntentFilter filter = new IntentFilter(SMS_SENT_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsSentReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(smsSentReceiver, filter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsSentReceiver);
    }
}
