package com.technova.help;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_Follow_Contact_Fragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] gotCNames = new String[1000];
    String[] gotCNos = new String[1000];
    int gotCCount = 0;
    public static Boolean searchFlag = false;
    public static EditText search;
    ProgressDialog pd;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0f;
    private double currentLongitude = 0.0f;

    public Tab_Follow_Contact_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab__follow__contact, container, false);

        pd = ProgressDialog.show(getContext(), "Loading Contacts",
                "Please Wait");


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


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerAllContacts);
        search = (EditText) view.findViewById(R.id.search);
        for (int i = 0; i < 999; i++) {
            gotCNames[i] = "";
            gotCNos[i] = "";
        }


        SharedPreferences runCheck = getContext().getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");

        Firebase.setAndroidContext(getContext());
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/profile");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                for (com.firebase.client.DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (int i = 0; i < HomeActivity.totalContact; i++) {
                        if (postSnapshot.getKey().contains(HomeActivity.contactNos[i]) && !(postSnapshot.getKey().contains(number))) {
                            //Toast.makeText(getContext(), i+" No:"+postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                            gotCNames[gotCCount] = HomeActivity.contactNames[i];
                            gotCNos[gotCCount] = HomeActivity.contactNos[i];
                            gotCCount++;
                        }
                    }

                }
                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new RecyclerViewAdapterContact(getDataSet(), getContext());
                mRecyclerView.setAdapter(mAdapter);
                pd.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Button iWantToFollowYou = (Button) view.findViewById(R.id.button_iWantToFollowU);
        iWantToFollowYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < gotCCount; i++) {
                    if (RecyclerViewAdapterContact.checkStates[i]) {
                        if (!(RecyclerViewAdapterContact.flag[i] == 1 || RecyclerViewAdapterContact.flag[i] == 2)) {
                            Firebase.setAndroidContext(view.getContext());
                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/" + RecyclerViewAdapterContact.choosedContacts[i] + "/" + number);
                            FollowMeAdapter followMeAdapter = new FollowMeAdapter(number, name, "pending");
                            ref.setValue(followMeAdapter);
                            Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                        } else if (RecyclerViewAdapterContact.flag[i] == 1) {
                            final String[] number = {""};
                            Firebase.setAndroidContext(view.getContext());
                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/CurrentLocation");
                            final int finalI = i;
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                                    for (com.firebase.client.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        CurrentLocation currentLocation = snapshot.getValue(CurrentLocation.class);
                                        if (currentLocation.getNumber().equals(RecyclerViewAdapterContact.choosedContacts[finalI])) {
                                            number[0] = currentLocation.getNumber();
                                        }
                                    }
                                    if (!number[0].equals(""))
                                        getContext().startActivity(new Intent(getContext(), TrackingActivity.class).putExtra("number", number[0]));
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Your request is pending", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });


        Button uFollowMe = (Button) view.findViewById(R.id.button_uFollowMe);
        uFollowMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < gotCCount; i++) {

                    if (RecyclerViewAdapterContact.checkStates[i]) {

                        Firebase.setAndroidContext(view.getContext());
                        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/FollowMe/"+ number+"/" + RecyclerViewAdapterContact.choosedContacts[i]);
                        FollowMeAdapter followMeAdapter = new FollowMeAdapter(RecyclerViewAdapterContact.choosedContacts[i], RecyclerViewAdapterContact.choosedNames[i], "accepted");
                        ref.setValue(followMeAdapter);

                        /***************************Tracking Logic Nihal************************/
                        int minTime = 5000;
                        LocationManager locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        MyLocationListener myLocListener = new MyLocationListener(view.getContext());
                        criteria.setPowerRequirement(Criteria.POWER_LOW);
                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                        criteria.setAltitudeRequired(false);
                        criteria.setBearingRequired(false);
                        criteria.setCostAllowed(true);
                        criteria.setSpeedRequired(false);
                        String bestProvider = locationManager.getBestProvider(criteria, false);
                        if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            return;
                        }
                        locationManager.requestLocationUpdates(bestProvider, minTime, 1, myLocListener);
                        SharedPreferences runCheck = view.getContext().getSharedPreferences("Name", 0); //load the preferences
                        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                        final String number = runCheck.getString("number", "no number");
                        final Firebase refNe = new Firebase("https://help-82424.firebaseio.com/CurrentLocation/" + number);
                        CurrentLocation currentLocation = new CurrentLocation(name, number, currentLatitude, currentLongitude);
                        refNe.setValue(currentLocation);
                        Toast.makeText(view.getContext(), "Now " + RecyclerViewAdapterContact.choosedNames[i] + " can see your location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        addTextListener();
        return view;
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < gotCCount; index++) {
            DataObject obj = new DataObject(gotCNames[index], gotCNos[index]);
            results.add(index, obj);
        }

        return results;
    }

    public void addTextListener() {

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList filteredList = new ArrayList<DataObject>();

                for (int i = 0; i < gotCCount; i++) {

                    final String text = gotCNames[i].toLowerCase();
                    final String num = gotCNos[i].toString();
                    if (text.contains(query) || num.contains(query)) {
                        searchFlag = true;
                        DataObject obj = new DataObject(gotCNames[i], gotCNos[i]);
                        filteredList.add(obj);
                    }
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter = new RecyclerViewAdapterContact(filteredList, getContext());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
