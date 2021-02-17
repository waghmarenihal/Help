package com.technova.help;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
/*import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;*/

import static android.R.attr.button;
import static com.technova.help.R.id.textView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_1_Activity extends Fragment implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] gotCNames, gotCNos;
    int gotCCount;
    static int reqCount = 0;
    public static Boolean searchFlag = false;
    public static EditText search;
    String locationLink;

    public Tab_1_Activity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_tab_1, container, false);
        gotCNames = HomeActivity.contactNames;
        gotCNos = HomeActivity.contactNos;
        gotCCount = HomeActivity.totalContact;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerAllContacts);
        search = (EditText) view.findViewById(R.id.search);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        addTextListener();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
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


        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fbAllContacts);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] finalContacts = new String[1000];
                int k = 0;
                for (int i = 0; i < 999; i++) {
                    if (!MyRecyclerViewAdapter.choosedContacts[i].equals("")) {
                        finalContacts[k] = MyRecyclerViewAdapter.choosedContacts[i];
                        k++;
                    }
                }
                //Send SMS
                if (k == 0) {
                    Toast.makeText(getContext(), "Please select at least one Contact Person", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < k; i++) {
                        try {
                            String helpMessage="Hi, I need your help.\n"
                                    + HomeActivity.reason + "\nPlease find me at my current location below:\n"+locationLink;


                            //helpMessage=shorten(helpMessage);
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(finalContacts[i], null,helpMessage , null, null);
 
                            SharedPreferences runCheck = view.getContext().getSharedPreferences("Name", 0); //load the preferences
                            final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                            final String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
                            Firebase.setAndroidContext(view.getContext());


                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/profile");
                            final int finalI = i;
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (finalContacts[finalI].contains(postSnapshot.getKey())) {

                                            Firebase dataRef = new Firebase("https://help-82424.firebaseio.com/Requests/" + finalContacts[finalI]);
                                            dataRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        Person person = postSnapshot.getValue(Person.class);
                                                        reqCount = person.getRequestCount();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {

                                                }
                                            });

                                            final Firebase refN = new Firebase("https://help-82424.firebaseio.com/PersonalHelp/" + finalContacts[finalI] + "/" + number);
                                            PersonalHelp personalHelp = new PersonalHelp(name, HomeActivity.reason, Double.toString(currentLatitude), Double.toString(currentLongitude), number, reqCount + 1);
                                            String keyN = refN.push().getKey();
                                            refN.child(keyN).setValue(personalHelp);
                                            final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
                                            //Creating Person object
                                            Person person = new Person();
                                            person.setName(name);
                                            person.setAddress(HomeActivity.reason);
                                            person.setLattitude(Double.toString(currentLatitude));
                                            person.setLongitude(Double.toString(currentLongitude));
                                            person.setRange("5");
                                            person.setStatus("Personal");
                                            person.setNumber(number);
                                            person.setLive("yes");
                                            person.setRequestCount(++reqCount);

                                            String key = ref.push().getKey();
                                            ref.child(key).setValue(person);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }
                    Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getContext(),HomeActivity.class));
                    getActivity().finish();
                }
            }
        });

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
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

    public void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //Toast.makeText(getContext(),"Position: "+position,Toast.LENGTH_SHORT).show();
            }
        });
        mGoogleApiClient.connect();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    /**
     * If connected get lat and long
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            locationLink="http://www.google.com/maps/place/" + currentLatitude + "," + currentLongitude;
            new RetrieveFeedTask().execute(locationLink);

            //Toast.makeText(getContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity().getParent(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        //Toast.makeText(getContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
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
                mAdapter = new MyRecyclerViewAdapter(filteredList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<String, String, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            Urlshortener.Builder builder = new Urlshortener.Builder(AndroidHttp.newCompatibleTransport(),
                    AndroidJsonFactory.getDefaultInstance(), null);
            Urlshortener urlshortener = builder.build();

            com.google.api.services.urlshortener.model.Url url = new com.google.api.services.urlshortener.model.Url();
            url.setLongUrl(urls[0]);
            try {
                Urlshortener.Url.Insert insert = urlshortener.url().insert(url);
                insert.setKey("AIzaSyBEqj6I5q9bhp-hzW6Cr-0nPfbtosFxsIM");
                url = insert.execute();
                return url.getId();
            } catch (IOException e) {
                //LogUtil.e(TAG, Log.getStackTraceString(e));
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            locationLink=feed;
        }
    }
    /*String shorten(String longUrl){

        Urlshortener.Builder builder = new Urlshortener.Builder (AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null);
        Urlshortener urlshortener = builder.build();

        com.google.api.services.urlshortener.model.Url url = new Url();
        url.setLongUrl(longUrl);
        try {
            url = urlshortener.url().insert(url).execute();
            return url.getId();
        } catch (IOException e) {
            return null;
        }
    }*/
}