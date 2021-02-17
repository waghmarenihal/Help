package com.technova.help;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technova.help.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import static android.R.attr.version;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup relAuth;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;
    private static Button mReLogin;

    CountryCodePicker countryCodePicker;

    ProgressBar progressBar;
    private DatabaseReference databaseReference;
    public static final String FB_DATABASE_PATH = "profile";
    public static int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.SEND_SMS,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.CALL_PHONE}, 1111);
            }
        }

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Assign views
        mPhoneNumberViews = (ViewGroup) findViewById(R.id.phone_auth_fields);
        mSignedInViews = (ViewGroup) findViewById(R.id.signed_in_buttons);
        relAuth = (ViewGroup) findViewById(R.id.rel_auth);

        mStatusText = (TextView) findViewById(R.id.status);
        mDetailText = (TextView) findViewById(R.id.detail);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mReLogin=(Button)findViewById(R.id.buttonRelogin);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        mPhoneNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPhoneNumberField.length() > 0) {
                    enableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    disableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#9E9E9E"));
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (mPhoneNumberField.getText().toString().contains(" ")) {
                    int indexOf = mPhoneNumberField.getText().toString().indexOf(" ");
                    mPhoneNumberField.getText().replace(indexOf, indexOf + 1, "");
                }

                if (mPhoneNumberField.length() > 13) {
                    mPhoneNumberField.setText(mPhoneNumberField.getText().toString().substring(0, 13));
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
                    mPhoneNumberField.setError(spannableStringBuilder);

                }

                //if (mPhoneNumberField.length()>)
                if (mPhoneNumberField.length() > 0) {
                    enableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    disableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#9E9E9E"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (mPhoneNumberField.getText().toString().contains(" ")) {
                    int indexOf = mPhoneNumberField.getText().toString().indexOf(" ");
                    mPhoneNumberField.getText().replace(indexOf, indexOf + 1, "");
                }
                if (mPhoneNumberField.length() > 0) {
                    enableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    disableViews(mStartButton);
                    mStartButton.setTextColor(Color.parseColor("#9E9E9E"));
                }
            }
        });


        countryCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(mPhoneNumberField);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
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
                    mPhoneNumberField.setError(spannableStringBuilder);

                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(countryCodePicker.getSelectedCountryCode()+mPhoneNumberField.getText().toString());
        }else {

        }
        // [END_EXCLUDE]
    }

    private void callRelogin() {
        LoginActivity.mReLogin.setVisibility(View.VISIBLE);
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
        mStatusText.setVisibility(View.INVISIBLE);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]

                                int errorColor;
                                final int version = Build.VERSION.SDK_INT;
                                if (version >= 23) {
                                    errorColor = ContextCompat.getColor(getApplicationContext(), R.color.errorColor);
                                } else {
                                    errorColor = getResources().getColor(R.color.errorColor);
                                }
                                String errorString = "Invalid code.";
                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                                spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                                mVerificationField.setError(errorString);
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }


    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mPhoneNumberField);
                disableViews(mStartButton, mVerifyButton, mResendButton, mVerificationField);
                mStartButton.setTextColor(Color.parseColor("#9E9E9E"));
                mVerifyButton.setTextColor(Color.parseColor("#9E9E9E"));
                mResendButton.setTextColor(Color.parseColor("#9E9E9E"));
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mVerifyButton.setTextColor(Color.parseColor("#FFFFFF"));
                mResendButton.setTextColor(Color.parseColor("#FFFFFF"));
                mDetailText.setText(R.string.status_code_sent);
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                //mDetailText.setText(R.string.status_verification_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText("Verification Successful");
                mDetailText.setTextColor(Color.parseColor("#43a047"));
                progressBar.setVisibility(View.INVISIBLE);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                        mVerificationField.setTextColor(Color.parseColor("#4bacb8"));
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                mDetailText.setTextColor(Color.parseColor("#dd2c00"));
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                mStatusText.setText(R.string.signed_in);
                break;
        }

        if (user == null) {
            // Signed out
            relAuth.setVisibility(View.VISIBLE);
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText(R.string.signed_out);
            ;
        } else {
            // Signed in
            relAuth.setVisibility(View.GONE);
            mPhoneNumberViews.setVisibility(View.GONE);
            databaseReference = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())) {
                            UserProfile person = ds.getValue(UserProfile.class);
                            SharedPreferences settings = getSharedPreferences("Name", 0);
                            SharedPreferences.Editor edit = settings.edit();
                            edit.clear();
                            edit.commit();
                            edit.putString("name", person.getUsername()); //set to has run
                            edit.putString("reg1", person.getReg1()); //set to has run
                            edit.putString("reg2", person.getReg2());
                            edit.putString("reg3", person.getReg3());
                            edit.putString("number", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            edit.commit();
                            flag = 1;
                        }
                    }

                    if (flag == 0) {
                        Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(10000);
                        //callRelogin();
                    }catch (Exception e){
//                        Toast.makeText(LoginActivity.this, "Try RE-LOGIN again", Toast.LENGTH_SHORT).show();
                    }finally {

                    }
                }
            });
            thread.start();
            callRelogin();
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
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
            mPhoneNumberField.setError(errorString);
            mPhoneNumberField.setTextColor(Color.parseColor("#ff1744"));
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }

                ///////hide keyboard start
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                /////////hide keyboard end


                mStatusText.setText("Authenticating....!");
                 progressBar.setVisibility(View.VISIBLE);
               // Toast.makeText(this, "+"+countryCodePicker.getSelectedCountryCode()+mPhoneNumberField.getText().toString(), Toast.LENGTH_SHORT).show();
                startPhoneNumberVerification("+"+countryCodePicker.getSelectedCountryCode()+mPhoneNumberField.getText().toString());
                break;
            case R.id.buttonRelogin:
                if (!validatePhoneNumber()) {
                    return;
                }

                ///////hide keyboard start
                InputMethodManager inputManager1 = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager1.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                /////////hide keyboard end


                mStatusText.setText("Authenticating....!");
                progressBar.setVisibility(View.VISIBLE);
                startPhoneNumberVerification(mPhoneNumberField.getText().toString());

                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    int errorColor;
                    final int version = Build.VERSION.SDK_INT;
                    if (version >= 23) {
                        errorColor = ContextCompat.getColor(getApplicationContext(), R.color.errorColor);
                    } else {
                        errorColor = getResources().getColor(R.color.errorColor);
                    }
                    String errorString = "Cannot be empty.";
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
                    spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
                    mVerificationField.setError(errorString);
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

}
