package com.technova.help;

/**
 * Created by N on 12/15/2017.
 */

public class FollowMeAdapter {

    public String number;
    public String name;
    public String state;

    public FollowMeAdapter() {
    }

    public FollowMeAdapter(String number, String name, String state) {

        this.number = number;
        this.name = name;
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }
}
