package com.example.careerpilot;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class jobmodels {
    public static class AdzunaResponse {
        @SerializedName("results")
        public List<JobItem> results;
    }

    public static class JobItem {
        public String title;
        public String description;
        @SerializedName("salary_min")
        public Double salary_min;
        public Company company;
        public Location location;
    }

    public static class Company {
        @SerializedName("display_name")
        public String display_name;
    }

    public static class Location {
        @SerializedName("display_name")
        public String display_name;
    }
}
