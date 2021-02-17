package com.technova.help;

public class UserProfile {
    public String reg1;
    public String reg2;
    public String reg3;
    public String username;
    public String url;
    public float rate;
    public int rateCount;



    public UserProfile(String reg1, String reg2, String reg3, String username,  float rate,int rateCount) {
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.reg3 = reg3;
        this.rate = rate;
        this.rateCount = rateCount;
        this.username = username;
    }

    public UserProfile() {
    }

    public int getRateCount() {
        return rateCount;
    }

    public String getReg1() {
        return reg1;
    }

    public String getReg2() {
        return reg2;
    }

    public String getReg3() {
        return reg3;
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }

    public float getRate() {
        return rate;
    }

}
