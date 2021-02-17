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

public class Responses extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    static String[] gotName = new String[1000];
    static String[] gotHelp = new String[1000];
    int gotCount;
    int flag = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewResponses);
        gotCount = 0;
        for (int i = 0; i < 999; i++) {
            gotName[i] = "";
            gotHelp[i] = "";
        }
        SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
        final String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
        final String number = runCheck.getString("number", "no number");

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (number.equals(postSnapshot.getKey())) {
                        Toast.makeText(Responses.this, "Key: " + postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(Responses.this, "Nope ", Toast.LENGTH_SHORT).show();
                    final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses/" + postSnapshot.getKey());
                    final String otherNumber = postSnapshot.getKey();
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                final int reqCount = Integer.parseInt(postSnapshot.getKey());
                                final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Responses/" + otherNumber + "/" + reqCount);
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            ResponseAdapter responseAdapter = postSnapshot.getValue(ResponseAdapter.class);
                                            if (responseAdapter.getHelperContact().equals(number)) {
                                                final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Requests/" + otherNumber);
                                                ref.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            //Toast.makeText(Responses.this, "Key: "+postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                                                            Person person = postSnapshot.getValue(Person.class);


                                                            if (person.getRequestCount() == reqCount) {
                                                                gotName[gotCount] = person.getName()+"("+person.getNumber()+")";
                                                                gotHelp[gotCount++] = person.getAddress()+":"+Integer.toString(person.getRequestCount());
                                                            }
                                                        }
                                                        callMe();
                                                    }

                                                    @Override
                                                    public void onCancelled(FirebaseError firebaseError) {

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
                    //}
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void callMe() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(Responses.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapterResponses(getDataSet(), Responses.this);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }


    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        int index = 0;
        for (int i = 0; i < gotCount; i++) {
            if (i>0){
                if (!(gotName[i].equals(gotName[i - 1]) && gotHelp[i].equals(gotHelp[i - 1]))) {
                    DataObject obj = new DataObject(gotName[i], gotHelp[i]);
                    results.add(index++, obj);
                }
            }else{
                DataObject obj = new DataObject(gotName[i], gotHelp[i]);
                results.add(index++, obj);
            }

        }
        gotCount=0;
        return results;
    }
}
