package com.technova.help;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    static double lon, lat;
    private GoogleMap mMap;
    static String name;
    Marker marker;
    TextView textView;
    static int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       /* lat=getIntent().getDoubleExtra("latitude",0);
        lon=getIntent().getDoubleExtra("longitude",0);
        name=getIntent().getStringExtra("name");*/
        textView = (TextView) findViewById(R.id.textViewMapReady);
        final String number = getIntent().getStringExtra("number");
        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/CurrentLocation");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CurrentLocation currentLocation = postSnapshot.getValue(CurrentLocation.class);
                    if (number.contains(currentLocation.getNumber())) {
                        //Toast.makeText(TrackingActivity.this, "number: "+postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                        lat = currentLocation.getLattitude();
                        lon = currentLocation.getLongitude();
                        name = currentLocation.getName();
                        //Toast.makeText(TrackingActivity.this, "Location Changed", Toast.LENGTH_SHORT).show();
                        marker.setPosition(new LatLng(lat, lon));
                        //Toast.makeText(TrackingActivity.this, "Called", Toast.LENGTH_SHORT).show();
                        flag = 1;
                        //finish();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lon);
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.humane_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        MarkerOptions a = new MarkerOptions()
                .position(new LatLng(50, 6))
                .title(name + " is here")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        marker = mMap.addMarker(a);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        if (ActivityCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackingActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);



        if (flag == 1) {
            textView.setVisibility(View.GONE);
        }

        //Toast.makeText(this, "MapReady", Toast.LENGTH_SHORT).show();
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
}
