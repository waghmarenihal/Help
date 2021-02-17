package com.technova.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by N on 12/26/2017.
 */

public class MyLocationListener implements LocationListener {

    Location location; // location
    Context context;

    public MyLocationListener(Context context) {
        this.context=context;
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            Toast.makeText(context, Double.toString(location.getLatitude())+" "+Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();

            SharedPreferences runCheck = context.getSharedPreferences("Name", 0); //load the preferences
            final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
            final String number = runCheck.getString("number", "no number");
            final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
            CurrentLocation currentLocation = new CurrentLocation(name, number, location.getLatitude(), location.getLongitude());
            refNe.setValue(currentLocation);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
