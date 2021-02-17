package com.technova.help;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class NewUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        final EditText username = (EditText) findViewById(R.id.et_NewUserUsername);
        final EditText password = (EditText) findViewById(R.id.et_NewUserUserPass);
        final EditText name = (EditText) findViewById(R.id.et_NewUserName);
        final EditText no = (EditText) findViewById(R.id.et_NewUserUserMobileNo);
        final EditText reg1 = (EditText) findViewById(R.id.et_NewUserReg1);
        final EditText reg2 = (EditText) findViewById(R.id.et_NewUserReg2);
        final EditText reg3 = (EditText) findViewById(R.id.et_NewUserReg3);

        Firebase.setAndroidContext(NewUserActivity.this);
        final Firebase ref = new Firebase("https://help-82424.firebaseio.com/Login");
        final Button reg = (Button) findViewById(R.id.buttonRegister);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (username.getText().toString().equals("") || password.getText().toString().equals("") || name.getText().toString().equals("")
                        || no.getText().toString().equals("") || reg1.getText().toString().equals("") || reg2.getText().toString().equals("")
                        || reg3.getText().toString().equals("")) {
                    Toast.makeText(NewUserActivity.this, "Please fill all details!", Toast.LENGTH_SHORT).show();
                } else {

                    final int[] flag = new int[1];
                    ref.child("Login");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                //Getting the data from snapshot
                                Login person = postSnapshot.getValue(Login.class);
                                if (person.getUName().equals(username.getText().toString()) && person.getNumber().equals(no.getText().toString())) {
                                    flag[0] = 1;
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });

                    if (flag[0] != 1) {
                        Login person = new Login();
                        person.setName(name.getText().toString());
                        person.setUName(username.getText().toString());
                        person.setNumber(no.getText().toString());
                        person.setAddress(password.getText().toString());
                        person.setReg1(reg1.getText().toString());
                        person.setReg2(reg2.getText().toString());
                        person.setReg3(reg3.getText().toString());
                        ref.child(name.getText().toString()).setValue(person);
                        Toast.makeText(NewUserActivity.this, "User Created Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(NewUserActivity.this, "User Already Exists!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }
}
