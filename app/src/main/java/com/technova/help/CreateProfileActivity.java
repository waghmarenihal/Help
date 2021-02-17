package com.technova.help;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technova.help.ImageSlider.ImageSliderActivity;

public class CreateProfileActivity extends AppCompatActivity {

    Uri filePath;
    ///Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference databaseReference;

    EditText editTextReg1, editTextReg2, editTextReg3;
    //public static final String FB_STORAGE_PATH = "profile/";
    public static final String FB_DATABASE_PATH = "profile";
    static String username, phonenumber;
    EditText editTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        editTextName = (EditText) findViewById(R.id.editTextNewUserName);
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextName.getText().toString().contains("  ")) {
                    int indexOf = editTextName.getText().toString().indexOf("  ");
                    editTextName.getText().replace(indexOf, indexOf + 2, " ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                username = editTextName.getText().toString();
            }
        });


        editTextReg1 = (EditText) findViewById(R.id.et_NewUserReg1);
        editTextReg1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextReg1.getText().toString().contains(" ")) {
                    int indexOf = editTextReg1.getText().toString().indexOf(" ");
                    editTextReg1.getText().replace(indexOf, indexOf + 1, "");
                }

                if (editTextReg1.length() > 13) {
                    editTextReg1.setText(editTextReg1.getText().toString().substring(0, 13));
                    int errorColor;
                    final int version = Build.VERSION.SDK_INT;
                    if (version >= 23) {
                        errorColor = ContextCompat.getColor(getApplicationContext(), R.color.errorColor);
                    } else {
                        errorColor = getResources().getColor(R.color.errorColor);
                    }
                    String errorString = "Invalid phone number.";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                    spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                    editTextReg1.setError(spannableStringBuilder);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editTextReg2 = (EditText) findViewById(R.id.et_NewUserReg2);
        editTextReg2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextReg2.getText().toString().contains(" ")) {
                    int indexOf = editTextReg2.getText().toString().indexOf(" ");
                    editTextReg2.getText().replace(indexOf, indexOf + 1, "");
                }

                if (editTextReg2.length() > 13) {
                    editTextReg2.setText(editTextReg2.getText().toString().substring(0, 13));
                    int errorColor;
                    final int version = Build.VERSION.SDK_INT;
                    if (version >= 23) {
                        errorColor = ContextCompat.getColor(getApplicationContext(), R.color.errorColor);
                    } else {
                        errorColor = getResources().getColor(R.color.errorColor);
                    }
                    String errorString = "Invalid phone number.";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                    spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                    editTextReg2.setError(spannableStringBuilder);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editTextReg3 = (EditText) findViewById(R.id.et_NewUserReg3);
        editTextReg3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editTextReg3.getText().toString().contains(" ")) {
                    int indexOf = editTextReg3.getText().toString().indexOf(" ");
                    editTextReg3.getText().replace(indexOf, indexOf + 1, "");
                }

                if (editTextReg3.length() > 13) {
                    editTextReg3.setText(editTextReg3.getText().toString().substring(0, 13));
                    int errorColor;
                    final int version = Build.VERSION.SDK_INT;
                    if (version >= 23) {
                        errorColor = ContextCompat.getColor(getApplicationContext(), R.color.errorColor);
                    } else {
                        errorColor = getResources().getColor(R.color.errorColor);
                    }
                    String errorString = "Invalid phone number.";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                    spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                    editTextReg3.setError(spannableStringBuilder);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        //filePath = Uri.parse(getIntent().getStringExtra("filepath"));
        //username = getIntent().getStringExtra("name");
        phonenumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Button button = (Button) findViewById(R.id.RegButtonCreate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        //displaying progress dialog while image is uploading
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        UserProfile imageUpload = new UserProfile(
                editTextReg1.getText().toString(),
                editTextReg2.getText().toString(),
                editTextReg3.getText().toString(),
                username,
                5, 1);

        ///Save Image info into firebase database
        // String uploadId = databaseReference.push().getKey();
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(imageUpload);
        Toast.makeText(CreateProfileActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

        SharedPreferences settings = getSharedPreferences("Name", 0);
        SharedPreferences.Editor edit = settings.edit();
        edit.clear();
        edit.commit();
        edit.putString("name", username); //set to has run
        edit.putString("reg1", editTextReg1.getText().toString()); //set to has run
        edit.putString("reg2", editTextReg2.getText().toString());
        edit.putString("reg3", editTextReg3.getText().toString());
        edit.putString("number", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        edit.commit();

        settings = getSharedPreferences("EmergencyFire", 0);
        edit = settings.edit();
        edit.clear();
        edit.commit();
        edit.putString("no", "101"); //set to has run
        edit.commit();

        settings = getSharedPreferences("EmergencyAmbulance", 0);
        edit = settings.edit();
        edit.clear();
        edit.commit();
        edit.putString("no", "102"); //set to has run
        edit.commit();

        settings = getSharedPreferences("EmergencyCop", 0);
        edit = settings.edit();
        edit.clear();
        edit.commit();
        edit.putString("no", "100"); //set to has run
        edit.commit();

        settings = getSharedPreferences("EmergencyNo", 0);
        edit = settings.edit();
        edit.clear();
        edit.commit();
        edit.putString("no", "911"); //set to has run
        edit.commit();
        progressDialog.dismiss();
        startActivity(new Intent(CreateProfileActivity.this, ImageSliderActivity.class));
        finish();


    }
}
