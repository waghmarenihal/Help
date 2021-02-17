package com.technova.help;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class FinalGroupActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String[] gotCNames, gotCNos;
    int gotCCount;
    int flag = 0;
    //public static Boolean[] checkMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_group);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerCreateGroupChoosedContacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new GroupRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        final EditText editText = (EditText) findViewById(R.id.et_GroupName);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fbCreateGroupFinal);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = (String) editText.getText().toString().trim();
                if (!groupName.equals("")) {
                    DBHelper dbHelper = new DBHelper(FinalGroupActivity.this);
                    String saveNames = CreateGroupActivity.finalContacts[0];
                    String saveNos = CreateGroupActivity.finalNos[0];
                    for (int i = 1; i < CreateGroupActivity.k; i++) {
                        if (!CreateGroupActivity.finalContacts[i].equals("")) {
                            saveNames = saveNames + ", " + CreateGroupActivity.finalContacts[i];
                            saveNos = saveNos + "," + CreateGroupActivity.finalNos[i];
                        }
                    }
                    if (!saveNames.equals("")) {
                        Cursor cursor = dbHelper.getGroup(groupName);
                        if (!cursor.moveToFirst()) {
                            dbHelper.insertContact(groupName, saveNames, saveNos);
                            Toast.makeText(FinalGroupActivity.this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(FinalGroupActivity.this, HomeActivity.class));
                            flag = 1;
                            finish();
                        } else {
                            Toast.makeText(FinalGroupActivity.this, "Group with same name already exists!!!", Toast.LENGTH_SHORT).show();
                        }
                       /* try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            startActivity(new Intent(FinalGroupActivity.this, HomeActivity.class));
                        }*/
                    } else {
                        Toast.makeText(FinalGroupActivity.this, "Select at least one Group Member", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FinalGroupActivity.this, "Please Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                //dbHelper.deleteContact("");

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (flag == 0)
            startActivity(new Intent(FinalGroupActivity.this, CreateGroupActivity.class));
        //new CreateGroupActivity().imDone();
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < gotCCount; index++) {
            DataObject obj = new DataObject(gotCNames[index], gotCNos[index]);
            results.add(index, obj);
        }
        return results;
    }
}
