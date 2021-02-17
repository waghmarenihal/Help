package com.technova.help;


import android.*;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_List_Fragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0.0f;
    private double currentLongitude = 0.0f;
    View myView;
    private RecyclerView mRecyclerViewPersonalHelp,mRecyclerViewHigh, mRecyclerViewMed, mRecyclerViewLow;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList filteredListPersonalHelp = new ArrayList<DataObject>();
    ArrayList filteredListHigh = new ArrayList<DataObject>();
    ArrayList filteredListMed = new ArrayList<DataObject>();
        ArrayList filteredListLow = new ArrayList<DataObject>();
        ProgressDialog progressDialog;

    public Tab_List_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab__list_, container, false);
        myView = view;

        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        mRecyclerViewPersonalHelp = (RecyclerView) view.findViewById(R.id.recyclerPersonalHelp);
        mRecyclerViewHigh = (RecyclerView) view.findViewById(R.id.recyclerHighPriority);
        mRecyclerViewMed = (RecyclerView) view.findViewById(R.id.recyclerMedPriority);
        mRecyclerViewLow = (RecyclerView) view.findViewById(R.id.recyclerLowPriority);


        SharedPreferences runCheck = view.getContext().getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
        Firebase.setAndroidContext(view.getContext());
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/PersonalHelp/"+number);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    final Firebase ref = new Firebase("https://help-82424.firebaseio.com/PersonalHelp/"+number+"/"+postSnapshot.getKey());
                    final String otherNumber=postSnapshot.getKey();
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                              PersonalHelp personalHelp=postSnapshot.getValue(PersonalHelp.class);
                                DataObject obj = new DataObject(personalHelp.getName()+ "(" + otherNumber + ")", personalHelp.getAddress());
                                filteredListPersonalHelp.add(obj);
                                mAdapter = new MyRecyclerAdapterPersonalHelp(filteredListPersonalHelp);
                                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(myView.getContext());
                                mRecyclerViewPersonalHelp.setLayoutManager(layoutmanager);
                                mRecyclerViewPersonalHelp.setItemAnimator(new DefaultItemAnimator());
                                mRecyclerViewPersonalHelp.setAdapter(mAdapter);
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
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(myView.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myView.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            //Value event listener for realtime data update


            SharedPreferences runCheck = getContext().getSharedPreferences("Name", 0); //load the preferences
            final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no

            Firebase.setAndroidContext(myView.getContext());
            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (final DataSnapshot postSnapshot : snapshot.getChildren()) {
                        //Getting the data from snapshot


                        final Firebase dataRef = new Firebase("https://help-82424.firebaseio.com/Requests/" + postSnapshot.getKey());
                        dataRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                for (DataSnapshot postSnapshotNew : dataSnapshot.getChildren()) {
                                    //Getting the data from snapshot

                                    Person person = postSnapshotNew.getValue(Person.class);
                                    if (!person.getName().equals(name)) {
                                        Location startPoint = new Location("locationA");
                                        startPoint.setLatitude(currentLatitude);
                                        startPoint.setLongitude(currentLongitude);

                                        Location endPoint = new Location("locationA");
                                        endPoint.setLatitude(Double.parseDouble(person.getLattitude()));
                                        endPoint.setLongitude(Double.parseDouble(person.getLongitude()));

                                        double distance = (startPoint.distanceTo(endPoint)) / 1000;
                                        if (distance <= Double.parseDouble(person.getRange())) {

                                            TextView textView = (TextView) myView.findViewById(R.id.tv_List);
                                            textView.setVisibility(View.INVISIBLE);
                                            if (person.getStatus().equals("High")) {
                                                DataObject obj = new DataObject(person.getName() + "(" + postSnapshot.getKey() + ")", person.getAddress());
                                                filteredListHigh.add(obj);
                                            }
                                            if (person.getStatus().equals("Med")) {
                                                DataObject obj = new DataObject(person.getName() + "(" + postSnapshot.getKey() + ")", person.getAddress());
                                                filteredListMed.add(obj);
                                                // Toast.makeText(view.getContext(), "Med", Toast.LENGTH_SHORT).show();
                                            }
                                            if (person.getStatus().equals("Low")) {
                                                DataObject obj = new DataObject(person.getName() + "(" + postSnapshot.getKey() + ")", person.getAddress());
                                                filteredListLow.add(obj);
                                                //Toast.makeText(view.getContext(), "Low", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } else {
                                        //code if the app HAS run before
                                        continue;
                                    }
                                }
                                //Toast.makeText(view.getContext(), filteredListHigh.toString(), Toast.LENGTH_SHORT).show();
                                mAdapter = new MyRecyclerAdapterList(filteredListHigh);
                                RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(myView.getContext());
                                mRecyclerViewHigh.setLayoutManager(layoutmanager);
                                mRecyclerViewHigh.setItemAnimator(new DefaultItemAnimator());
                                mRecyclerViewHigh.setAdapter(mAdapter);

                                mAdapter = new MyRecyclerAdapterList(filteredListMed);
                                RecyclerView.LayoutManager layoutmanagerMed = new LinearLayoutManager(myView.getContext());
                                mRecyclerViewMed.setLayoutManager(layoutmanagerMed);
                                mRecyclerViewMed.setItemAnimator(new DefaultItemAnimator());
                                mRecyclerViewMed.setAdapter(mAdapter);

                                mAdapter = new MyRecyclerAdapterList(filteredListLow);
                                RecyclerView.LayoutManager layoutmanagerLow = new LinearLayoutManager(myView.getContext());
                                mRecyclerViewLow.setLayoutManager(layoutmanagerLow);
                                mRecyclerViewLow.setItemAnimator(new DefaultItemAnimator());
                                mRecyclerViewLow.setAdapter(mAdapter);
                                progressDialog.dismiss();
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
        filteredListHigh.clear();
        filteredListLow.clear();
        filteredListMed.clear();
        filteredListPersonalHelp.clear();
    }
}
