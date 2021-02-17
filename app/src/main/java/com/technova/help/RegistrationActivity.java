package com.technova.help;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSignOutButton;
    private FirebaseAuth mAuth;
    Uri filePath;
    ImageButton imageButton;
    ImageView imageView;
    EditText editTextName;
    CardView cardView;

    final int PICK_IMAGE_REQUEST = 9999;
    TextView textViewName, textViewNo, textViewPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        imageButton = (ImageButton) findViewById(R.id.imageButtonNewUserProfile);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageViewNewUserImage);
        editTextName = (EditText) findViewById(R.id.editTextNewUserName);
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editTextName.getText().toString().contains("  ")){
                    int indexOf=editTextName.getText().toString().indexOf("  ");
                    editTextName.getText().replace(indexOf,indexOf+2," ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewNo = (TextView) findViewById(R.id.textViewNo);
        textViewPreview = (TextView) findViewById(R.id.textViewNewuserPreview);
        cardView = (CardView) findViewById(R.id.cardViewNewUser);
        Button buttonNext = (Button) findViewById(R.id.RegButtonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextName.getText().toString().equals("")||filePath==null){
                    Toast.makeText(RegistrationActivity.this, "Please enter the required details", Toast.LENGTH_SHORT).show();
                }else {
                startActivity(new Intent(RegistrationActivity.this,CreateProfileActivity.class)
                        .putExtra("name",editTextName.getText().toString())
                        .putExtra("filepath",filePath.toString()));
                finish();}
            }
        });

    }

    private void signOut() {
        mAuth.signOut();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                signOut();
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
        }
    }


    private void chooseImage() {
        Intent i = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            //Toast.makeText(RegistrationActivity.this,filePath.toString(),Toast.LENGTH_LONG).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Drawable d = new BitmapDrawable(getResources(), bitmap);
                imageView.setBackgroundDrawable(d);
                textViewName.setText(editTextName.getText().toString());
                textViewNo.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                textViewPreview.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

