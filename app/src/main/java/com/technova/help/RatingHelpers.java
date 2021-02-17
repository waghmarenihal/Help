package com.technova.help;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class RatingHelpers extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    static String[] gotName = new String[1000];
    static String[] gotComment = new String[1000];
    static String[] takeRating = new String[1000];
    static String[] gotRating = new String[1000];
    int gotCount;
    RatingBar ratingBar;
    ProgressDialog progressDialog;
    float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_helpers);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        /*ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rate = v;
            }
        });*/
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerRank);
        Button button = (Button) findViewById(R.id.buttonSaveRank);


        gotCount = 0;
        for (int i = 0; i < 999; i++) {
            gotName[i] = "";
            gotComment[i] = "";
            takeRating[i] = "0";
            gotRating[i] = "";
        }
        SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses/" + number + "/" + getIntent().getStringExtra("reqCount"));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ResponseAdapter responses = postSnapshot.getValue(ResponseAdapter.class);
                    if (!responses.getHelperContact().equals(number)) {
                        gotName[gotCount++] = responses.getHelperName() + "(" + responses.getHelperContact() + ")";
                    }

                }
                callMe();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] flag=new int[1000];
                for (int i = 0; i < gotCount; i++) {
                    flag[i]=0;
                    if (Float.parseFloat(takeRating[i]) > 0) {
                        //Toast.makeText(RatingHelpers.this, "Name: " + gotName[i].substring(gotName[i].indexOf("(") + 1, gotName[i].indexOf(")")) + " Rate: " + takeRating[i], Toast.LENGTH_SHORT).show();
                        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/profile");
                        final int finalI = i;
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().equals(gotName[finalI].substring(gotName[finalI].indexOf("(") + 1, gotName[finalI].indexOf(")"))) && flag[finalI]==0) {
                                        flag[finalI]=1;
                                        UserProfile profile = postSnapshot.getValue(UserProfile.class);
                                        float rate = profile.getRate();
                                        int count = profile.rateCount;
                                        float finalRate = (Float.parseFloat(takeRating[finalI]) + rate);
                                        UserProfile putUserProfile = new UserProfile(profile.getReg1(), profile.getReg2(), profile.getReg3(), profile.getUsername(), finalRate, count + 1);
                                        ref.child(postSnapshot.getKey()).setValue(putUserProfile);
                                       // Toast.makeText(RatingHelpers.this, "Done ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }
                }
                SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
                final String number = runCheck.getString("number", "no number");

                Firebase.setAndroidContext(RatingHelpers.this);
                final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final Person person = postSnapshot.getValue(Person.class);
                            if (person.getAddress().equals(getIntent().getStringExtra("address"))){
                                postSnapshot.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                Firebase refResponse = new Firebase("https://help-82424.firebaseio.com/Responses/" + number+"/"+getIntent().getStringExtra("reqCount"));
                //refResponse.child(getIntent().getStringExtra("reqCount"));
                refResponse.removeValue();
                Firebase refPersonalHelp = new Firebase("https://help-82424.firebaseio.com/PersonalHelp");
                //refResponse.child(getIntent().getStringExtra("reqCount"));
                refPersonalHelp.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Firebase refPersonalHelp = new Firebase("https://help-82424.firebaseio.com/PersonalHelp/" + postSnapshot.getKey()+"/"+number);
                            //refResponse.child(getIntent().getStringExtra("reqCount"));
                            refPersonalHelp.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final PersonalHelp person = postSnapshot.getValue(PersonalHelp.class);
                                        if (person.getAddress().equals(getIntent().getStringExtra("address"))){
                                            postSnapshot.getRef().removeValue();
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

                Toast.makeText(RatingHelpers.this, "Thank You For Your Feedback", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void callMe() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(RatingHelpers.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapterRating(getDataSet(), RatingHelpers.this);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        int index = 0;
        for (int i = 0; i < gotCount; i++) {
            if (i > 0) {
                if (!(gotName[i].equals(gotName[i - 1]) && gotComment[i].equals(gotComment[i - 1]))) {
                    DataObject obj = new DataObject(gotName[i], gotComment[i]);
                    results.add(index++, obj);
                }
            } else {
                DataObject obj = new DataObject(gotName[i], gotComment[i]);
                results.add(index++, obj);
            }

        }
        return results;
    }

}
