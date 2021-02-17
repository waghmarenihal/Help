package com.technova.help;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.id.list;

public class HelpOptionActivity extends AppCompatActivity {

    public static String[] contactNames = new String[1000];
    public static String[] contactNos = new String[1000];
    public static int totalContact = 0;
    public static String reason = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_option);

        ImageButton ibHarrasment = (ImageButton) findViewById(R.id.imageButtonHarrasment);
        ibHarrasment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I faced an Harrasment!!!";
            }
        });
        ImageButton ibHazardousMat = (ImageButton) findViewById(R.id.imageButtonHazardousMaterial);
        ibHazardousMat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "There's a Hazardous Material nearby me!!!";
            }
        });
        ImageButton ibTheft = (ImageButton) findViewById(R.id.imageButtonTheft);
        ibTheft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I have been Robbed by a Thief!";
            }
        });
        ImageButton ibSafety = (ImageButton) findViewById(R.id.imageButtonSafetyHazard);
        ibSafety.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I do not Feel Safe!!!";
            }
        });
        ImageButton ibSuspicious = (ImageButton) findViewById(R.id.imageButtonSuspicious);
        ibSuspicious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I sense some Suspicious Activity!!!";
            }
        });
        ImageButton ibPropoerty = (ImageButton) findViewById(R.id.imageButtonProperty);
        ibPropoerty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "My Property is Damaged!!!";
            }
        });
        ImageButton ibMaintainance = (ImageButton) findViewById(R.id.imageButtonMaintanance);
        ibMaintainance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I have some Maintainance Work to do!!!";
            }
        });
        ImageButton ibWalk = (ImageButton) findViewById(R.id.imageButtonWalkSafe);
        ibWalk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "I could not Walk Safely!!!";
            }
        });
        ImageButton ibDoor = (ImageButton) findViewById(R.id.imageButtonDoorUnlocked);
        ibDoor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();
                reason = "My Door is Locked!!!";
            }
        });
        Button bCancel = (Button) findViewById(R.id.bCancelOption);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.etEmergency);
                if (!editText.getText().toString().equals("")) {
                    LoadContactsAyscn lca = new LoadContactsAyscn();
                    lca.execute();
                    reason = editText.getText().toString();
                } else {
                    Toast.makeText(HelpOptionActivity.this,"Please enter the message!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        for (int i = 0; i < 999; i++) {
            contactNames[i] = "";
            contactNos[i] = "";
        }
        totalContact=0;
    }

    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<String>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(HelpOptionActivity.this, "Loading Contacts",
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
                if(phNumber.contains(" ")){
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
            Intent intent = new Intent(HelpOptionActivity.this, ChooseContactActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}