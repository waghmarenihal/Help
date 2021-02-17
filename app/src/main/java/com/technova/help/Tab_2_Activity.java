package com.technova.help;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_2_Activity extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] smsNos = new String[1000];

    TextView textView;
    FloatingActionButton floatingActionButton;
    public static EditText search;
    String locationLink;

    public Tab_2_Activity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_tab_2, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerGroupContacts);
        search = (EditText) view.findViewById(R.id.searchGroup);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fbGroupSMS);
        textView = (TextView) view.findViewById(R.id.tv_GroupAbsent);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CreateGroupRecycler(getDataSet());
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int k = 0;
                String[] finalNos = new String[1000];
                for (int i = 0; i < 999; i++) {
                    if (!CreateGroupRecycler.choosedGroups[i].equals("")) {
                        k++;
                    }
                }
                //Send SMS
                if (k == 0) {
                    Toast.makeText(getContext(), "Please select at least one Group", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < k; i++) {
                        //try {
                        String mainNos = smsNos[i];
                        int startIndex = 0;
                        int endIndex = mainNos.length();
                        String newNo = mainNos.substring(startIndex, mainNos.indexOf(","));
                        String newString = mainNos.substring(mainNos.indexOf(",") + 1, endIndex);
                        while (true) {
                            SmsManager smsManager = SmsManager.getDefault();
                            //Toast.makeText(getContext(),newNo,Toast.LENGTH_SHORT).show();

                            String helpMessage="Hi, I need your help.\n"
                                    + HomeActivity.reason + "\nPlease find me at my current location below:\n"+locationLink;

                            smsManager.sendTextMessage(newNo, null, helpMessage, null, null);
                            if (newString.contains(",")) {
                                newNo = newString.substring(startIndex, mainNos.indexOf(","));
                                endIndex = newString.length();
                                newString = newString.substring(newString.indexOf(",") + 1, endIndex);
                                //Toast.makeText(getContext(),newString,Toast.LENGTH_SHORT).show();
                                //break;
                            } else {
                                //Toast.makeText(getContext(),newString,Toast.LENGTH_SHORT).show();
                                smsManager.sendTextMessage(newString, null, helpMessage, null, null);
                                break;
                            }
                        }
                        SmsManager smsManager = SmsManager.getDefault();
                        //smsManager.sendTextMessage(smsNos[i], null, "Hi, I need our help.\n" + HelpOptionActivity.reason + "\nPlease find me at my current location below:\n http://www.google.com/maps/place/" + currentLatitude + "," + currentLongitude, null, null);
                        /*} catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }*/
                    }
                    Toast.makeText(getContext(), "Message Sent", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getContext(), HomeActivity.class));
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
        DBHelper dbHelper = new DBHelper(getContext());
        int index = 0;
        Cursor cursor = dbHelper.getAllGroups();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String gName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                String gPerson = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_PERSON));
                String gNo = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_NUMBER));
                smsNos[index] = gNo;
                //Toast.makeText(getContext(),gNo,Toast.LENGTH_SHORT).show();
                DataObject obj = new DataObject(gName, gPerson);
                results.add(index, obj);
                index++;
                cursor.moveToNext();
            }
        } else {
            textView.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
        }
        return results;
    }

    public void onResume() {
        super.onResume();
        ((CreateGroupRecycler) mAdapter).setOnItemClickListener(new CreateGroupRecycler.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
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
            //Toast.makeText(getContext(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();


            locationLink="http://www.google.com/maps/place/" + currentLatitude + "," + currentLongitude;
            new RetrieveFeedTask().execute(locationLink);
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

                DBHelper dbHelper = new DBHelper(getContext());
                Cursor cursor = dbHelper.getGroup(query.toString());
                if (cursor.moveToFirst()) {
                    int index = 0;
                    while (!cursor.isAfterLast()) {
                        String gName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                        String gPerson = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_PERSON));
                        String gNo = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_NUMBER));
                        smsNos[index] = gNo;
                        //Toast.makeText(getContext(),gNo,Toast.LENGTH_SHORT).show();
                        DataObject obj = new DataObject(gName, gPerson);
                        filteredList.add(index, obj);
                        index++;
                        cursor.moveToNext();
                    }
                } else {
                    cursor = dbHelper.getGroupMember(query.toString());
                    if (cursor.moveToFirst()) {
                        int index = 0;
                        while (!cursor.isAfterLast()) {
                            String gName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                            String gPerson = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_PERSON));
                            String gNo = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_NUMBER));
                            smsNos[index] = gNo;
                            //Toast.makeText(getContext(),gNo,Toast.LENGTH_SHORT).show();
                            DataObject obj = new DataObject(gName, gPerson);
                            filteredList.add(index, obj);
                            index++;
                            cursor.moveToNext();
                        }
                    } else {
                        cursor = dbHelper.getGroupNo(query.toString());
                        if (cursor.moveToFirst()) {
                            int index = 0;
                            while (!cursor.isAfterLast()) {
                                String gName = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                                String gPerson = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_PERSON));
                                String gNo = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CONTACT_NUMBER));
                                smsNos[index] = gNo;
                                //Toast.makeText(getContext(),gNo,Toast.LENGTH_SHORT).show();
                                DataObject obj = new DataObject(gName, gPerson);
                                filteredList.add(index, obj);
                                index++;
                                cursor.moveToNext();
                            }
                        }
                    }
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mAdapter = new CreateGroupRecycler(filteredList);
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
}
