package com.example.careerpilot;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

// Correct Retrofit Imports
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class Searchforjobs extends AppCompatActivity {

    private TextView tvSalaryTrend;
    private RecyclerView rvJobList;
    private JobAdapter adapter;

    // Credentials from Adzuna Developer Portal
    private final String APP_ID = "33443ca2";
    private final String APP_KEY = "b89bc00c0bd0e097fa5b9d55427451b5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchforjobs);

        // 1. Initialize UI Elements
        tvSalaryTrend = findViewById(R.id.tv_salary_trend);
        rvJobList = findViewById(R.id.rv_job_list);

        // 2. Setup RecyclerView
        rvJobList.setLayoutManager(new LinearLayoutManager(this));

        // 3. Start Data Fetching
        loadMarketData();
    }

    private void loadMarketData() {
        // FIX 404: The baseUrl MUST end with a forward slash /
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.adzuna.com/v1/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AdzunaApi api = retrofit.create(AdzunaApi.class);

        // Fetching Software jobs in Kenya ('ke')
        api.searchJobs("ke", 1, APP_ID, APP_KEY, "Software").enqueue(new Callback<jobmodels.AdzunaResponse>() {
            @Override
            public void onResponse(Call<jobmodels.AdzunaResponse> call, Response<jobmodels.AdzunaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    // This triggers if the URL is wrong (404) or Keys are invalid (403)
                    Toast.makeText(Searchforjobs.this, "Server Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<jobmodels.AdzunaResponse> call, Throwable t) {
                Toast.makeText(Searchforjobs.this, "Network Error: Check Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(jobmodels.AdzunaResponse data) {
        if (data.results == null || data.results.isEmpty()) {
            tvSalaryTrend.setText("No live data found for this region.");
            return;
        }

        // 1. Set the List Adapter
        adapter = new JobAdapter(data.results);
        rvJobList.setAdapter(adapter);

        // 2. Calculate Salary Trend (Average of salary_min)
        double totalSalary = 0;
        int count = 0;

        for (jobmodels.JobItem job : data.results) {
            if (job.salary_min != null) {
                totalSalary += job.salary_min;
                count++;
            }
        }

        if (count > 0) {
            double average = totalSalary / count;
            // Display formatted salary in KES
            tvSalaryTrend.setText(String.format(Locale.getDefault(), "Avg Entry Salary: KES %.0f", average));
        } else {
            tvSalaryTrend.setText("Market Data: Connected (Salaries hidden)");
        }
    }

    /**
     * Interface for Adzuna API calls
     */
    interface AdzunaApi {
        // FIX 404: The Path must NOT start with a forward slash /
        @GET("jobs/{country}/search/{page}")
        Call<jobmodels.AdzunaResponse> searchJobs(
                @Path("country") String country,
                @Path("page") int page,
                @Query("app_id") String id,
                @Query("app_key") String key,
                @Query("what") String query
        );
    }
}