package com.technova.help;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        Animation.AnimationListener {

    ImageButton askHelp, giveHelp, createGroup, satisfied;
    TextView fire, ambulance, cop, emergency, followMe;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    public static String[] contactNames = new String[1000];
    public static String[] contactNos = new String[1000];
    public static int totalContact = 0;
    public static String reason = "";
    public static String reg1 = "";
    public static String reg2 = "";
    public static String reg3 = "";
    public static String hasRun = "";
    public static String callFire = "";
    public static String callAmbulance = "";
    public static String callCop = "";
    public static String call911 = "";
    static int count = 0;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    String locationLink;
    // Animation
    Animation animFadein, animFadeout;
    int flag = 0, flagAnim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);
        // set animation listener
        animFadein.setAnimationListener(this);
        animFadeout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);
        // set animation listener
        animFadeout.setAnimationListener(this);

        SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
        hasRun = runCheck.getString("name", null); //see if it's run before, default no
        reg1 = runCheck.getString("reg1", null); //see if it's run before, default no
        reg2 = runCheck.getString("reg2", null); //see if it's run before, default no
        reg3 = runCheck.getString("reg3", null); //see if it's run before, default no

        /*if (hasRun==null) {
            //code for if this is the first time the app has run
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        } else {
            //code if the app HAS run before
        }*/
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.INTERNET,
                        Manifest.permission.CALL_PHONE}, 1111);
            }
        }

        SharedPreferences runCall = getSharedPreferences("EmergencyFire", 0); //load the preferences
        callFire = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyAmbulance", 0); //load the preferences
        callAmbulance = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyCop", 0); //load the preferences
        callCop = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyNo", 0); //load the preferences
        call911 = runCall.getString("no", null); //see if it's run before, default no

        final int[] sentFlag = {0};

        ImageButton imageButtonEmerg = (ImageButton) findViewById(R.id.ibEmergency);
        imageButtonEmerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reg1.equals("")) {
                    if (isValidPhone(reg1)) {
                        //String name = prefs.getString("no", "0");//"No name defined" is the default value.
                        // String no1=name.substring(0,name.indexOf(","));
                        //String no2=name.substring(name.indexOf(",")+1,name.length());
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String helpMessage="EMERGENCY, NEED YOUR URGENT HELP AT BELOW LOCATION:" + locationLink;
                            smsManager.sendTextMessage(reg1, null,helpMessage , null, null);
                            sentFlag[0] = 1;
                            //Toast.makeText(HomeActivity.this, reg1, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(HomeActivity.this, "Emergency number 1 is invalid", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }else {
                        Toast.makeText(HomeActivity.this, "Emergency number 1 is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!reg2.equals("")) {

                    if (isValidPhone(reg2)) {
                        //String name = prefs.getString("no", "0");//"No name defined" is the default value.
                        // String no1=name.substring(0,name.indexOf(","));
                        //String no2=name.substring(name.indexOf(",")+1,name.length());
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String helpMessage="EMERGENCY, NEED YOUR URGENT HELP AT BELOW LOCATION:" + locationLink;
                            smsManager.sendTextMessage(reg2, null, helpMessage, null, null);
                            sentFlag[0] = 1;
                            //Toast.makeText(HomeActivity.this, reg2, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(HomeActivity.this, "Emergency number 2 is invalid", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }else {
                        Toast.makeText(HomeActivity.this, "Emergency number 2 is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!reg3.equals("")) {

                    if (isValidPhone(reg3)) {
                        //String name = prefs.getString("no", "0");//"No name defined" is the default value.
                        // String no1=name.substring(0,name.indexOf(","));
                        //String no2=name.substring(name.indexOf(",")+1,name.length());
                        Toast.makeText(HomeActivity.this, reg3, Toast.LENGTH_SHORT).show();
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String helpMessage="EMERGENCY, NEED YOUR URGENT HELP AT BELOW LOCATION:" + locationLink;
                            smsManager.sendTextMessage(reg3, null, helpMessage, null, null);
                            sentFlag[0] = 1;
                            //Toast.makeText(HomeActivity.this, reg3, Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(HomeActivity.this, "Emergency number 3 is invalid", Toast.LENGTH_SHORT).show();
                            ex.printStackTrace();
                        }
                    }else {
                        Toast.makeText(HomeActivity.this, "Emergency number 3 is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                if (sentFlag[0] == 1)
                    Toast.makeText(HomeActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
            }
        });


        askHelp = (ImageButton) findViewById(R.id.imageButtonAskHelp);
        giveHelp = (ImageButton) findViewById(R.id.imageButtonGiveHelp);
        satisfied = (ImageButton) findViewById(R.id.ibSatisfied);
        fire = (TextView) findViewById(R.id.ib_Home_Fire);
        ambulance = (TextView) findViewById(R.id.ib_Home_Ambulance);
        cop = (TextView) findViewById(R.id.ib_Home_Cop);
        emergency = (TextView) findViewById(R.id.ib_Home_911);
        followMe = (TextView) findViewById(R.id.ibFollowMe);
        //final ImageView imageViewHomeGreenButtonAbove = (ImageView) findViewById(R.id.imageViewHomeGreenButtonAbove);
        final TextView textViewHomeGreenButtonAbove = (TextView) findViewById(R.id.textViewHomeGreenButtonAbove);


        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no

       /* Firebase.setAndroidContext(this);
        final Firebase refText = new Firebase("https://help-82424.firebaseio.com/Requests");
        refText.addValueEventListener(new ValueEventListener() {
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
                                        count++;
                                        //Toast.makeText(HomeActivity.this, "Count:"+count, Toast.LENGTH_SHORT).show();
                                        textViewHomeGreenButtonAbove.setText(Integer.toString(count));
                                    }

                                } else {
                                    //code if the app HAS run before
                                    continue;
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
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
*/

        followMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
            }
        });
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
            }
        });
        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MyRequests.class));
            }
        });
        cop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Responses.class));
            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.helpline_numbers);
                Window window = dialog.getWindow();
                window.setTitle("Helpline Number");
                window.setLayout(500, 400);

                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View promptsView = li.inflate(R.layout.helpline_numbers, null);

                ImageView cop = (ImageView) dialog.findViewById(R.id.helpline_cop);
                cop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(HomeActivity.this, "Hotay Bhava "+callCop, Toast.LENGTH_SHORT).show();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + callCop));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //use checkSelfPermission()
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                        119);
                                startActivity(callIntent);
                            }
                        } else {
                            //simply use the required feature
                            //as the user has already granted permission to them during installation
                        }
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

                ImageView ambulance = (ImageView) dialog.findViewById(R.id.helpline_ambulance);
                ambulance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(HomeActivity.this, "Hotay Bhava "+callCop, Toast.LENGTH_SHORT).show();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + callAmbulance));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //use checkSelfPermission()
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                        119);
                                startActivity(callIntent);
                            }
                        } else {
                            //simply use the required feature
                            //as the user has already granted permission to them during installation
                        }
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

                ImageView fire = (ImageView) dialog.findViewById(R.id.helpline_fire);
                fire.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(HomeActivity.this, "Hotay Bhava "+callCop, Toast.LENGTH_SHORT).show();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + callFire));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //use checkSelfPermission()
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                        119);
                                startActivity(callIntent);
                            }
                        } else {
                            //simply use the required feature
                            //as the user has already granted permission to them during installation
                        }
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

                ImageView emergency = (ImageView) dialog.findViewById(R.id.helpline_call911);
                emergency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(HomeActivity.this, "Hotay Bhava "+callCop, Toast.LENGTH_SHORT).show();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + call911));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //use checkSelfPermission()
                            if (ActivityCompat.checkSelfPermission(HomeActivity.this,
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                                        119);
                                startActivity(callIntent);
                            }
                        } else {
                            //simply use the required feature
                            //as the user has already granted permission to them during installation
                        }
                        startActivity(callIntent);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        Firebase.setAndroidContext(HomeActivity.this);
        final Firebase ref = new Firebase(Config.FIREBASE_URL);

        ref.child("Person");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    Person person = postSnapshot.getValue(Person.class);
                    if (person.getLive().equals("yes") && person.getName().equals(hasRun)) {
                        satisfied.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        satisfied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.child(hasRun).removeValue();
                satisfied.setVisibility(View.INVISIBLE);
                Toast.makeText(HomeActivity.this, "Happy To Help You!!!", Toast.LENGTH_LONG).show();
            }
        });

        askHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(HomeActivity.this);
                View promptsView = li.inflate(R.layout.custom, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        HomeActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                /**
                 * userInput.addTextChangedListener(new TextWatcher() {
                 public void afterTextChanged(Editable s) {
                 }

                 public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 }

                 public void onTextChanged(CharSequence query, int start, int before, int count) {
                 query = query.toString().toLowerCase();
                 if (query.length()>30){
                 String fit=userInput.getText().toString();
                 fit=fit.substring(0,29);
                 userInput.setText(fit);
                 Toast.makeText(HomeActivity.this, "Message Limit Reached", Toast.LENGTH_SHORT).show();
                 }
                 }
                 });*/
                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(HomeActivity.this, "Enter Message", Toast.LENGTH_SHORT).show();
                                        } else {
                                            flag = 0;
                                            LoadContactsAyscn lca = new LoadContactsAyscn();
                                            lca.execute();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                alertDialog.getWindow().setBackgroundDrawableResource(R.color.dark);
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
        giveHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GiveHelpActivity.class);
                startActivity(intent);
                textViewHomeGreenButtonAbove.setVisibility(View.GONE);
                //imageViewHomeGreenButtonAbove.setVisibility(View.GONE);
            }
        });
        createGroup = (ImageButton) findViewById(R.id.ibProfile);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
            }
        });
        for (int i = 0; i < 999; i++) {
            contactNames[i] = "";
            contactNos[i] = "";
        }
        totalContact = 0;


        mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
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


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto GPS Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent, 999);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(HomeActivity.this, "Please Enable GPS for Proper functioning", Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999 && resultCode == 0) {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider != null && !provider.isEmpty()) {
                //Log.v(TAG, " Location providers: "+provider);
                //Start searching for location and update the location text when update available.
// Do whatever you want
                //startFetchingLocation();
                Toast.makeText(HomeActivity.this, "GPS Enabled!", Toast.LENGTH_SHORT).show();
            } else {
                //Users did not switch on the GPS
                Toast.makeText(HomeActivity.this, "Please Enable GPS for Proper functioning", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(HomeActivity.this.getParent(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        SharedPreferences runCall = getSharedPreferences("EmergencyFire", 0); //load the preferences
        callFire = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyAmbulance", 0); //load the preferences
        callAmbulance = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyCop", 0); //load the preferences
        callCop = runCall.getString("no", null); //see if it's run before, default no
        runCall = getSharedPreferences("EmergencyNo", 0); //load the preferences
        call911 = runCall.getString("no", null); //see if it's run before, default no
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (flagAnim == 1) {
            fire.startAnimation(animFadeout);
            flagAnim = 0;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(HomeActivity.this, "Loading Contacts",
                    "Please Wait");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ArrayList<String> contacts = new ArrayList<String>();
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, sortOrder);
            while (c.moveToNext()) {

                String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Apl Logic
                if (phNumber.contains(" ")) {
                    while (true) {
                        int index_space = phNumber.indexOf(" ");
                        if (index_space >= 0) {
                            phNumber = phNumber.substring(0, index_space) + phNumber.substring(index_space + 1, phNumber.length());
                        } else {
                            break;
                        }
                    }
                }
                int flag = 0;
                for (int i = 0; i < 999; i++) {
                    if (contactNames[i].equals(contactName)) {
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    contactNames[totalContact] = contactName;
                    contactNos[totalContact] = phNumber;
                    totalContact++;
                    contacts.add(contactName + ":" + phNumber);
                }


            }
            c.close();

            return contacts;

        }

        @Override
        protected void onPostExecute(ArrayList<String> contacts) {
            // TODO Auto-generated method stub
            super.onPostExecute(contacts);
            pd.cancel();
            if (flag == 1) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            } else if (flag == 2) {
                startActivity(new Intent(HomeActivity.this, FollowMe.class));
            } else {
                Intent intent = new Intent(HomeActivity.this, ChooseContactActivity.class);
                startActivity(intent);
            }
        }

    }


    public static boolean isValidPhone(String phone) {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
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
