package com.technova.help;

/**
 * Created by N on 12/26/2017.
 */

public class PersonalHelp {

    private String name;
    private String address;
    private String lattitude;
    private String longitude;
    private String number;
    private int requestCount;

    public int getRequestCount() {
        return requestCount;
    }

    public PersonalHelp() {
    }

    public PersonalHelp(String name, String address, String lattitude, String longitude, String number,int requestCount) {
        this.name = name;
        this.requestCount=requestCount;
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLattitude() {
        return lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNumber() {
        return number;
    }
}
