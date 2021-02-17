package com.technova.help;

/**
 * Created by N on 9/5/2017.
 */

public class Person {
    //name and address string
    private String name;
    private String address;
    private String range;
    private String lattitude;
    private String longitude;
    private String number;
    private String status;
    private String live;
    private int requestCount;

    public Person(String name, String address, String range, String lattitude, String longitude, String number, String status, String live, int requestCount) {
        this.name = name;
        this.address = address;
        this.range = range;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.number = number;
        this.status = status;
        this.live = live;
        this.requestCount = requestCount;
    }

    public Person() {
      /*Blank default constructor essential for Firebase*/
    }

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }



    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

}
