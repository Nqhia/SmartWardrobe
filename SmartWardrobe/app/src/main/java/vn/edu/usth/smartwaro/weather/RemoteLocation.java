package vn.edu.usth.smartwaro.weather;

public class RemoteLocation {
    private String name;
    private String region;
    private String country;
    private double lat;
    private double lon;

    public RemoteLocation(String name, String region, String country, double lat, double lon) {
        this.name = name;
        this.region = region;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
