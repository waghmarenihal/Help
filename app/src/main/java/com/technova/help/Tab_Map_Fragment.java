package com.technova.help;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_Map_Fragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    View MyView;
    Firebase ref;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0f;
    private double currentLongitude = 0.0f;

    public Tab_Map_Fragment() {
        // Required empty public constructor
    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) { //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab__map, container, false);
        MyView = view;
        try {
            // Inflate the layout for this fragment


            turnGPSOn();


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
        } catch (Exception e) {

        }
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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


            Firebase.setAndroidContext(MyView.getContext());
            ref = new Firebase("https://help-82424.firebaseio.com/Requests");

            //Value event listener for realtime data update
            ref.child("Person");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (final DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Getting the data from snapshot


                        final Firebase dataRef = new Firebase("https://help-82424.firebaseio.com/Requests/" + postSnapshot.getKey());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    //Getting the data from snapshot
                                    Person person = postSnapshot.getValue(Person.class);

                                    SharedPreferences runCheck = MyView.getContext().getSharedPreferences("Name", 0); //load the preferences
                                    final String hasRun = runCheck.getString("name", "null"); //see if it's run before, default no
                                    if (!hasRun.equals(person.getName())) {
                                        Location startPoint = new Location("locationA");
                                        startPoint.setLatitude(currentLatitude);
                                        startPoint.setLongitude(currentLongitude);

                                        Location endPoint = new Location("locationA");
                                        endPoint.setLatitude(Double.parseDouble(person.getLattitude()));
                                        endPoint.setLongitude(Double.parseDouble(person.getLongitude()));

                                        double distance = (startPoint.distanceTo(endPoint)) / 1000;
                                        if (distance <= Double.parseDouble(person.getRange())) {

                                            if (person.getStatus().equals("High")) {
                                                int height = 100;
                                                int width = 100;
                                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.red_marker);
                                                Bitmap b = bitmapdraw.getBitmap();
                                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(person.getLattitude()), Double.parseDouble(person.getLongitude())))
                                                        .anchor(0.5f, 0.5f)
                                                        .title(person.getName())
                                                        .snippet(person.getAddress())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        /*mMap.addCircle(new CircleOptions()
                                .center(new LatLng(Double.parseDouble(person.getLattitude()), Double.parseDouble(person.getLongitude())))
                                .radius(100)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE));*/
                                            }
                                            if (person.getStatus().equals("Med")) {
                                                int height = 100;
                                                int width = 100;
                                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.orange_marker);
                                                Bitmap b = bitmapdraw.getBitmap();
                                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(person.getLattitude()), Double.parseDouble(person.getLongitude())))
                                                        .anchor(0.5f, 0.5f)
                                                        .title(person.getName())
                                                        .snippet(person.getAddress())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                            }
                                            if (person.getStatus().equals("Low")) {
                                                int height = 100;
                                                int width = 100;
                                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.yellow_marker);
                                                Bitmap b = bitmapdraw.getBitmap();
                                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(person.getLattitude()), Double.parseDouble(person.getLongitude())))
                                                        .anchor(0.5f, 0.5f)
                                                        .title(person.getName())
                                                        .snippet(person.getAddress())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                            }
                                        }

                                    } else {
                                        //code if the app HAS run before
                                    }

                                }

                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(final Marker marker) {
                                        //Toast.makeText(getContext(), marker.getTitle()+" "+marker.getSnippet(), Toast.LENGTH_SHORT).show();
                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                    final Firebase dataRef = new Firebase("https://help-82424.firebaseio.com/Requests/" + dataSnapshot1.getKey());
                                                    dataRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                //Getting the data from snapshot
                                                                final Person person = postSnapshot.getValue(Person.class);

                                                                if (person.getName().equals(marker.getTitle()) && person.getAddress().equals(marker.getSnippet())) {
                                                                    final TextView name = (TextView) MyView.findViewById(R.id.textView_AskHelp_Name);
                                                                    final TextView emergency = (TextView) MyView.findViewById(R.id.textView_AskHelp_HELP);

                                                                    name.setText(person.getName());
                                                                    emergency.setText(person.getAddress());
                                                                    emergency.setVisibility(View.VISIBLE);
                                                                    final Button reply = (Button) MyView.findViewById(R.id.button_MapsReply);
                                                                    reply.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {

                                                                            SharedPreferences runCheck = getContext().getSharedPreferences("Name", 0); //load the preferences
                                                                            String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                                                                            String number = runCheck.getString("number", "No Name"); //see if it's run before, default no

                                                                            startActivity(new Intent(MyView.getContext(), AllResponses.class)
                                                                                    .putExtra("number", "(" + person.getNumber() + ")")
                                                                                    .putExtra("help", ":" + Integer.toString(person.getRequestCount())));
                                                                        }
                                                                    });
                                                                    final Button help = (Button) MyView.findViewById(R.id.button_AskHelp_HELP);
                                                                    help.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + Float.parseFloat(person.getLattitude()) + "," + Float.parseFloat(person.getLongitude())));
                                                                            view.getContext().startActivity(intent);
                                                                        }
                                                                    });


                                                                    final Button imReady = (Button) MyView.findViewById(R.id.buttonMapsImReady);
                                                                    imReady.setVisibility(View.VISIBLE);
                                                                    imReady.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            imReady.setVisibility(View.GONE);
                                                                            help.setVisibility(View.VISIBLE);
                                                                            reply.setVisibility(View.VISIBLE);

                                                                            Firebase.setAndroidContext(getContext());
                                                                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses/" + person.getNumber() + "/" + Integer.toString(person.getRequestCount()));

                                                                            SharedPreferences runCheck = getContext().getSharedPreferences("Name", 0); //load the preferences
                                                                            String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                                                                            String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
                                                                            ResponseAdapter responseAdapter = new ResponseAdapter(name, number, "I'm Ready To Help!");
                                                                            String key = ref.push().getKey();
                                                                            ref.child(key).setValue(responseAdapter);
                                                                        }
                                                                    });
                                                                    final Button cancel = (Button) MyView.findViewById(R.id.button_AskHelp_Cancel);
                                                                    cancel.setVisibility(View.VISIBLE);
                                                                    cancel.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            name.setText("Select any person...");
                                                                            emergency.setVisibility(View.INVISIBLE);
                                                                           // help.setVisibility(View.INVISIBLE);
                                                                            imReady.setVisibility(View.INVISIBLE);
                                                                            //reply.setVisibility(View.INVISIBLE);
                                                                            cancel.setVisibility(View.INVISIBLE);
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(FirebaseError firebaseError) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {

                                            }
                                        });
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

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
        //getActivity().finish();
    }
}
