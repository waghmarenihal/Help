package com.technova.help;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] gotCNames, gotCNos;
    int gotCCount;
    public static Boolean[] checkMe;
    public static String[] finalContacts = new String[1000];
    public static String[] finalNos = new String[1000];
    public static int k = 0;
    int flag = 0;
    public static EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        gotCNames = HomeActivity.contactNames;
        gotCNos = HomeActivity.contactNos;
        gotCCount = HomeActivity.totalContact;
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerCreateGroupAllContacts);
        search = (EditText) findViewById(R.id.searchMembers);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        addTextListener();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fbCreateGroupAllContacts);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 999; i++) {
                    if (!MyRecyclerViewAdapter.choosedNames[i].equals("")) {
                        String mainString = MyRecyclerViewAdapter.choosedNames[i];
                        /*if (mainString.contains(" ")) {
                            String subString = mainString.substring(0, mainString.indexOf(" "));
                            finalContacts[k] = subString;
                            finalNos[k] = MyRecyclerViewAdapter.choosedContacts[i];
                            k++;
                        } else {*/
                        finalContacts[k] = mainString;
                        finalNos[k] = MyRecyclerViewAdapter.choosedContacts[i];
                        k++;
                        //}

                    }
                }
                //Send SMS
                if (k == 0) {
                    Toast.makeText(CreateGroupActivity.this, "Please select at least one Contact Person", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CreateGroupActivity.this, FinalGroupActivity.class);
                    startActivity(intent);
                    flag = 1;
                    finish();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        k = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (flag == 0) {
            //startActivity(new Intent(CreateGroupActivity.this, ProfileActivity.class));
            finish();
        }
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
                        DataObject obj = new DataObject(gotCNames[i], gotCNos[i]);
                        filteredList.add(obj);
                    }
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
                mAdapter = new MyRecyclerViewAdapter(filteredList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
}
