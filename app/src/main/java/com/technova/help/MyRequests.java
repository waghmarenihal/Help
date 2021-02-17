package com.technova.help;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MyRequests extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    static String[] gotCount = new String[1000];
    static String[] gotReq = new String[1000];
    public static int[] count = new int[1000];
    int gotReqCount;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewRequests);
        gotReqCount = 0;
        for (int i = 0; i < 999; i++) {
            gotCount[i] = "0";
            gotReq[i] = "";
        }

        SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
        final String number = runCheck.getString("number", "no number");

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + number);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Person person = postSnapshot.getValue(Person.class);
                    gotReq[gotReqCount] = person.getAddress();
                    count[gotReqCount++] = person.getRequestCount();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        final Firebase refNew = new Firebase("https://help-82424.firebaseio.com/Responses/" + number);
        refNew.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    int i = 0;
                    for (int j = 0; j < gotReqCount; j++) {
                        if (count[i] == Integer.parseInt(postSnapshot.getKey())) {
                            gotCount[i++] = Long.toString(postSnapshot.getChildrenCount());
                        } else {
                            i++;
                        }
                    }
                }
                callMe();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void callMe() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MyRequests.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapterRequests(getDataSet(), MyRequests.this);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }


    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < gotReqCount; index++) {
            DataObject obj = new DataObject(gotReq[index], gotCount[index]);
            results.add(index, obj);
        }
        gotReqCount=0;
        return results;
    }
}