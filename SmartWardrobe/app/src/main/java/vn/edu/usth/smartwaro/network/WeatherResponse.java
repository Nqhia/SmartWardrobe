package vn.edu.usth.smartwaro.network;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("location")
    private Location location;

    @SerializedName("current")
    private Current current;

    public Location getLocation() {
        return location;
    }

    public Current getCurrent() {
        return current;
    }

    public static class Location {
        @SerializedName("name")
        private String name;

        @SerializedName("country")
        private String country;

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }
    }

    public static class Current {
        @SerializedName("temp_c")
        private double tempC;

        @SerializedName("condition")
        private Condition condition;

        public double getTempC() {
            return tempC;
        }

        public Condition getCondition() {
            return condition;
        }
    }

    public static class Condition {
        @SerializedName("text")
        private String text;

        public String getText() {
            return text;

        }
    }
}
