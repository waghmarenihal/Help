package com.technova.help;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MyService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0f;
    private double currentLongitude = 0.0f;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // Toast.makeText(this, " Uploading your location ", Toast.LENGTH_LONG).show();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        SharedPreferences runCheck = this.getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");
        final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
        CurrentLocation currentLocation = new CurrentLocation(name, number, currentLatitude, currentLongitude);
        refNe.setValue(currentLocation);
    }

    @Override
    public void onStart(Intent intent, int startId) {
      //  Toast.makeText(this, " MyService Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Servics Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            SharedPreferences runCheck = getApplicationContext().getSharedPreferences("Name", 0); //load the preferences
            final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
            final String number = runCheck.getString("number", "no number");
            final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
            CurrentLocation currentLocation = new CurrentLocation(name, number, currentLatitude, currentLongitude);
            refNe.setValue(currentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        SharedPreferences runCheck = getApplicationContext().getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");
        final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
        CurrentLocation currentLocation = new CurrentLocation(name, number, currentLatitude, currentLongitude);
        refNe.setValue(currentLocation);
    }

    public void onResume() {
        mGoogleApiClient.connect();
    }

    public void onPause() {
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}