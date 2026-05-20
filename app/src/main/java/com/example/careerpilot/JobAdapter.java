package com.example.careerpilot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<jobmodels.JobItem> jobList;

    public JobAdapter(List<jobmodels.JobItem> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using a built-in Android layout that has two text lines (text1 and text2)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        jobmodels.JobItem job = jobList.get(position);

        holder.title.setText(job.title);

        String company = (job.company != null) ? job.company.display_name : "Company not listed";
        String salary = (job.salary_min != null) ? " | KES " + String.format("%.0f", job.salary_min) : "";

        holder.subtitle.setText(company + salary);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;

        JobViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            subtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}