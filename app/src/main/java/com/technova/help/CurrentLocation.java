package com.technova.help;

/**
 * Created by N on 12/18/2017.
 */

public class CurrentLocation {
    private String name;
    private String number;
    private double lattitude;
    private double longitude;

    public CurrentLocation(String name, String number, double lattitude, double longitude) {
        this.name = name;
        this.number = number;
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public CurrentLocation() {
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
