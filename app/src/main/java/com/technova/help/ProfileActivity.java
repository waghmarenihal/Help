package com.technova.help;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collections;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    //public static final String FB_STORAGE_PATH = "profile/";
    public static final String FB_DATABASE_PATH = "profile";

    static String regNeW1 = "";
    static String regNeW2 = "";
    static String regNeW3 = "";
    static String nameNeW = "", phonenumber;
    static int flag = 0;
    static int change = 0;
    static float rateToatl;
    static int count;
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final TextView name = (TextView) findViewById(R.id.textViewProfileName);
        final TextView no = (TextView) findViewById(R.id.textViewProfileNo);
        final TextView reg1 = (TextView) findViewById(R.id.textViewProfileReg1);
        final TextView reg2 = (TextView) findViewById(R.id.textViewProfileReg2);
        final TextView reg3 = (TextView) findViewById(R.id.textViewProfileReg3);
        final TextView fire = (TextView) findViewById(R.id.textViewFire);
        final TextView ambulance = (TextView) findViewById(R.id.textViewAmbulance);
        final TextView cop = (TextView) findViewById(R.id.textViewCop);
        final TextView emergency = (TextView) findViewById(R.id.textView911);
        final TextView rate = (TextView) findViewById(R.id.textViewProfileRate);
        //imageView = (ImageView) findViewById(R.id.imageViewNewUserImage);
        phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        final Button button = (Button) findViewById(R.id.buttonSave);
        ImageButton signOut = (ImageButton) findViewById(R.id.ibSignOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        ImageButton group = (ImageButton) findViewById(R.id.ibCreateGroup);
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, CreateGroupActivity.class));
            }
        });

        SharedPreferences settings = getSharedPreferences("EmergencyFire", 0);
        fire.setText(settings.getString("no", "102"));
        settings = getSharedPreferences("EmergencyAmbulance", 0);
        ambulance.setText(settings.getString("no", "101"));
        settings = getSharedPreferences("EmergencyCop", 0);
        cop.setText(settings.getString("no", "100"));
        settings = getSharedPreferences("EmergencyNo", 0);
        emergency.setText(settings.getString("no", "911"));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //storage = FirebaseStorage.getInstance();
        //storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        change = 0;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Fetch Image data from firebase database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Toast.makeText(ProfileActivity.this,phone, Toast.LENGTH_SHORT).show();
                    if (phone.equals(snapshot.getKey()) && change == 0) {
                        UserProfile img = snapshot.getValue(UserProfile.class);
                        name.setText(img.getUsername());
                        nameNeW = img.getUsername();
                        no.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                        reg1.setText(img.getReg1());
                        regNeW1 = img.getReg1();
                        reg2.setText(img.getReg2());
                        regNeW2 = img.getReg2();
                        reg3.setText(img.getReg3());
                        regNeW3 = img.getReg3();
                        rateToatl = img.getRate();
                        count = img.getRateCount();
                        rate.setText(new DecimalFormat("#.#").format((rateToatl / count)) + " / 5");
                        //filePath = Uri.parse(img.getUrl());
                        //Toast.makeText(ProfileActivity.this, "Loading " + filePath.toString(), Toast.LENGTH_SHORT).show();
                        //Glide.with(ProfileActivity.this).load(img.getUrl()).into(imageView);
                        //imageView.setBackground(null);
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });*/

        reg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(
                        ProfileActivity.this, R.style.AppTheme));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            regNeW1 = userInput.getText().toString();
                                            reg1.setText(regNeW1);
                                            flag = 1;
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });


        reg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(
                        ProfileActivity.this, R.style.AppTheme));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            regNeW2 = userInput.getText().toString();
                                            reg2.setText(regNeW2);
                                            flag = 1;
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        reg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(
                        ProfileActivity.this, R.style.AppTheme));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            regNeW3 = userInput.getText().toString();
                                            reg3.setText(regNeW3);
                                            flag = 1;
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.call, null);

                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(
                        ProfileActivity.this, R.style.AppTheme));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                userInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (userInput.length() > 30) {
                            String fit = userInput.getText().toString();
                            fit = fit.substring(0, 29);
                            userInput.setText(fit);
                            Toast.makeText(ProfileActivity.this, "Keep short name", Toast.LENGTH_SHORT).show();
                        }

                        if (userInput.getText().toString().contains("  ")) {
                            int indexOf = userInput.getText().toString().indexOf("  ");
                            userInput.getText().replace(indexOf, indexOf + 2, " ");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text

                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the name", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else {
                                            nameNeW = userInput.getText().toString();
                                            name.setText(nameNeW);
                                            flag = 1;
                                            button.setTextColor(Color.WHITE);
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.color.dark);

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences("EmergencyFire", 0);
                                            SharedPreferences.Editor edit = settings.edit();
                                            edit.clear();
                                            edit.commit();
                                            edit.putString("no", userInput.getText().toString()); //set to has run
                                            edit.commit();
                                            Toast.makeText(ProfileActivity.this, "Emergency Fire Contact Number Saved", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            fire.setText(reason);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
        ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences("EmergencyAmbulance", 0);
                                            SharedPreferences.Editor edit = settings.edit();
                                            edit.clear();
                                            edit.commit();
                                            edit.putString("no", userInput.getText().toString()); //set to has run
                                            edit.commit();
                                            Toast.makeText(ProfileActivity.this, "Emergency Ambulance Contact Number Saved", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            ambulance.setText(reason);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
        cop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences("EmergencyCop", 0);
                                            SharedPreferences.Editor edit = settings.edit();
                                            edit.clear();
                                            edit.commit();
                                            edit.putString("no", userInput.getText().toString()); //set to has run
                                            edit.commit();
                                            Toast.makeText(ProfileActivity.this, "Emergency Police Contact Number Saved", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            cop.setText(reason);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ProfileActivity.this);
                View promptsView = li.inflate(R.layout.custom_call, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ProfileActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                //userInput.setHint("Enter Contact Number");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Set",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        String reason = userInput.getText().toString();
                                        if (reason.equals("")) {
                                            Toast.makeText(ProfileActivity.this, "Please enter the number", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                        } else if (reason.length() < 10 || reason.length() > 15) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            Toast.makeText(ProfileActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                        } else {
                                            SharedPreferences settings = getSharedPreferences("EmergencyNo", 0);
                                            SharedPreferences.Editor edit = settings.edit();
                                            edit.clear();
                                            edit.commit();
                                            edit.putString("no", userInput.getText().toString()); //set to has run
                                            edit.commit();
                                            Toast.makeText(ProfileActivity.this, "Emergency Contact Number Saved", Toast.LENGTH_SHORT).show();
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                                            emergency.setText(reason);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                userInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            }
        });


        phonenumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        button.setTextColor(getResources().getColor(R.color.colorPrimary));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    uploadFile();
            }
        });

    }

    private void uploadFile() {
        UserProfile imageUpload = new UserProfile(
                regNeW1,
                regNeW2,
                regNeW3,
                nameNeW, rateToatl, count);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).setValue(imageUpload);
        Toast.makeText(ProfileActivity.this, "Changes Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flag == 1) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to save changes?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadFile();
                                }
                            });
            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }
}
