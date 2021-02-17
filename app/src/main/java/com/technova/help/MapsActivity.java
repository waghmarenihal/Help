package com.technova.help;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    Float myLatt = 0.0f, myLong = 0.0f;
    String name, emergency, askerNumber, reqCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        myLatt = getIntent().getFloatExtra("lattitude", 0.0f);
        myLong = getIntent().getFloatExtra("logitude", 0.0f);
        name = getIntent().getStringExtra("name");
        reqCount = getIntent().getStringExtra("reqNo");
        askerNumber = getIntent().getStringExtra("number");
        emergency = getIntent().getStringExtra("emergency");

        TextView HelpAskerName = (TextView) findViewById(R.id.textView_AskHelp_Name);
        HelpAskerName.setText(name);

        TextView HelpWanted = (TextView) findViewById(R.id.textView_AskHelp_HELP);
        HelpWanted.setText(emergency);


        final Button button = (Button) findViewById(R.id.button_AskHelp_HELP);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MapsActivity.this, "Open Map", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + myLatt + "," + myLong));
                startActivity(intent);
                finish();
            }
        });


        final Button buttonCancel = (Button) findViewById(R.id.button_AskHelp_Cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            //   for ActivityCompat#requestPermissions for more details.
            return;
        }
//        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);


        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses/" + askerNumber + "/" + reqCount);


        final Button reply = (Button) findViewById(R.id.button_MapsReply);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, AllResponses.class)
                        .putExtra("number", "(" + getIntent().getStringExtra("number") + ")")
                        .putExtra("help", ":" + getIntent().getStringExtra("reqCount")));
            }
        });

        final Button imReady = (Button) findViewById(R.id.buttonMapsImReady);
        imReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imReady.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                reply.setVisibility(View.VISIBLE);

                SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
                String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
                ResponseAdapter responseAdapter = new ResponseAdapter(name, number, "I'm Ready To Help!");
                String key = ref.push().getKey();
                ref.child(key).setValue(responseAdapter);
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //Toast.makeText(MapsActivity.this,myLatt.toString(),Toast.LENGTH_SHORT).show();
        LatLng sydney = new LatLng(myLatt, myLong);
        mMap.addMarker(new MarkerOptions().position(sydney).title("This is My Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

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

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
}
