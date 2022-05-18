package com.sherlock.database.data;

public class StoreCoordinate {

    double latitude;
    double longitude;
    String icon;
    String aisleName;

    public StoreCoordinate(double latitude, double longitude, String icon, String aisleName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = icon;
        this.aisleName = aisleName;
    }

    public StoreCoordinate() {
    }

    public String getAisleName() {
        return aisleName;
    }

    public void setAisleName(String aisleName) {
        this.aisleName = aisleName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
