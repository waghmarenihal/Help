package com.technova.help;


import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_Others_Fragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    View MyView;
    EditText search;
    CircleOptions circle;
    Circle mapCircle;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0f;
    private double currentLongitude = 0.0f;
    String status = "Med";
    static int reqCount=0;
    private DatabaseReference databaseReference;

    public Tab_Others_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab__others, container, false);
        MyView = view;
        try {
            final RadioButton red = (RadioButton) view.findViewById(R.id.radioButtonHigh);
            red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    red.setChecked(true);
                    status = "High";
                }
            });
            final RadioButton orange = (RadioButton) view.findViewById(R.id.radioButtonMed);
            orange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orange.setChecked(true);
                    status = "Med";
                }
            });
            final RadioButton yellow = (RadioButton) view.findViewById(R.id.radioButtonLow);
            yellow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    yellow.setChecked(true);
                    status = "Low";
                }
            });
            orange.setChecked(true);
            status = "Med";
            search = (EditText) view.findViewById(R.id.editText_Range);

            TextView textViewHelp = (TextView) view.findViewById(R.id.textView_AskHelp_HELP);
            TextView textViewHelpName = (TextView) view.findViewById(R.id.textView_AskHelp_Name);
            SharedPreferences runCheck = view.getContext().getSharedPreferences("Name", 0); //load the preferences
            final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
            final String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
            textViewHelpName.setText(name);
            textViewHelp.setText(HomeActivity.reason);

            Firebase dataRef = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Person person = postSnapshot.getValue(Person.class);
                        reqCount=person.getRequestCount();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });


            Button button = (Button) view.findViewById(R.id.button_InsertData);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Firebase.setAndroidContext(view.getContext());
                    final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
                    //Creating Person object
                    Person person = new Person();
                    person.setName(name);
                    person.setAddress(HomeActivity.reason);
                    person.setLattitude(Double.toString(currentLatitude));
                    person.setLongitude(Double.toString(currentLongitude));
                    if (search.getText().toString().equals("")) {
                        person.setRange("5");
                    } else {
                        person.setRange(search.getText().toString());
                    }
                    person.setStatus(status);
                    person.setNumber(number);
                    person.setLive("yes");
                    person.setRequestCount(++reqCount);

                    String key = ref.push().getKey();
                    ref.child(key).setValue(person);
                    Toast.makeText(view.getContext(), "Message Sent Successfully!!!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            });
            addTextListener();
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Invalid Range", Toast.LENGTH_SHORT).show();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(view.getContext())
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

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(currentLatitude, currentLongitude);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("This is My Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        if (ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            //Toast.makeText(getContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            LatLng sydney = new LatLng(currentLatitude, currentLongitude);
            //mMap.addMarker(new MarkerOptions().position(sydney).title("This is My Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            if (ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MyView.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;

            }
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(currentLatitude, currentLongitude))
                    .anchor(0.5f, 0.5f)
                    .title("Your Current Location"));
            circle = new CircleOptions()
                    .center(new LatLng(currentLatitude, currentLongitude))
                    .radius(5000)
                    .strokeColor(Color.argb(128, 249, 83, 83))
                    .fillColor(Color.argb(128, 204, 255, 255));
            mapCircle = mMap.addCircle(circle);
            mMap.setMyLocationEnabled(true);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void addTextListener() {

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try {
                    query = query.toString().toLowerCase();
                    if (query.equals("")) {
                        mapCircle.remove();
                        mapCircle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(currentLatitude, currentLongitude))
                                .radius(5000)
                                .strokeColor(Color.argb(128, 249, 83, 83))
                                .fillColor(Color.argb(128, 204, 255, 255)));
                    } else {
                        mapCircle.remove();
                        Double rad = Double.parseDouble(query.toString()) * 1000;
                        mapCircle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(currentLatitude, currentLongitude))
                                .radius(rad)
                                .strokeColor(Color.argb(128, 249, 83, 83))
                                .fillColor(Color.argb(128, 204, 255, 255)));
                    }

                } catch (Exception e) {
                }
            }
        });
    }
}
