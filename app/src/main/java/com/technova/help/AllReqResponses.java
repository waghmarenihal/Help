package com.technova.help;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class AllReqResponses extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    static String[] gotName = new String[1000];
    static String[] gotComment = new String[1000];
    public static String[] gotNumber = new String[1000];
    int gotReplyCount;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_req_responses);
        // Toast.makeText(this, "Count: "+getIntent().getIntExtra("reqCount",0), Toast.LENGTH_SHORT).show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerAll);
        gotReplyCount = 0;

        final EditText editText = (EditText) findViewById(R.id.editTextAllReqResp);
        SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
        final String number = runCheck.getString("number", "no number");
        final Firebase refNew = new Firebase("https://help-82424.firebaseio.com/Responses/" + number + "/" + getIntent().getIntExtra("reqCount", 0));
        refNew.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ResponseAdapter responseAdapter = postSnapshot.getValue(ResponseAdapter.class);
                    if (responseAdapter.getHelperContact().equals(number)) {
                        gotName[gotReplyCount] = "You";
                    } else {
                        gotName[gotReplyCount] = responseAdapter.getHelperName();
                    }
                    gotNumber[gotReplyCount] = responseAdapter.getHelperContact();
                    gotComment[gotReplyCount++] = responseAdapter.getComment();
                }
                callMe();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fbAllSend);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Firebase refNew = new Firebase("https://help-82424.firebaseio.com/Responses/" + number + "/" + getIntent().getIntExtra("reqCount", 0));
                SharedPreferences runCheck = getSharedPreferences("Name", 0); //load the preferences
                String name = runCheck.getString("name", "No Name"); //see if it's run before, default no
                String number = runCheck.getString("number", "No Name"); //see if it's run before, default no
                ResponseAdapter responseAdapter = new ResponseAdapter(name, number, editText.getText().toString());
                String key = refNew.push().getKey();
                refNew.child(key).setValue(responseAdapter);
                mRecyclerView.setAdapter(null);
                callMe();
            }
        });
    }

    private void callMe() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(AllReqResponses.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapterAllReqResponse(getDataSet(), AllReqResponses.this);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }


    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < gotReplyCount; index++) {
            DataObject obj = new DataObject(gotName[index] + "(" + gotNumber[index] + ")", gotComment[index]);
            results.add(index, obj);
        }
        return results;
    }
}
