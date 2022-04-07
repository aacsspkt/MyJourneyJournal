package com.example.myjourneyjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myjourneyjournal.db.DbHelper;
import com.example.myjourneyjournal.model.JourneyModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EditJourney extends AppCompatActivity {
    public static final String TAG = EditJourney.class.getSimpleName();
    public static final String ID = "Id";
    public static final int PICK_IMAGE = 100;
    private JourneyModel journey;
    private Uri imageUri;

    private Bundle intentData;
    private DbHelper dbHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private EditText txtTitle, txtDesc, txtLocation;
    private ImageView imShowImage;

    public static Intent getIntent(Context context, int id) {
        Intent intent = new Intent(context, EditJourney.class);
        intent.putExtra(ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        // init object fields
        dbHelper = new DbHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        intentData = getIntent().getExtras();

        // init views
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDesc);
        txtLocation = findViewById(R.id.txtLocation);
        imShowImage = findViewById(R.id.showImage);
        ImageView imUploadImage = findViewById(R.id.imUploadImage);
        ImageView imAddLocation = findViewById(R.id.imAddLocation);
        ImageButton ibBack = findViewById(R.id.btn_back);
        Button btnSubmit = findViewById(R.id.btn_submit);

        // setup click listeners
        // Here, listeners are function with specific signature or simply - structure.
        // These methods are called method reference which it is as its name applies;
        // means methods are not called here, their names are simply referenced respectively.
        imUploadImage.setOnClickListener(this::promptToChooseImage);
        imAddLocation.setOnClickListener(this::promptToAddLocation);
        ibBack.setOnClickListener(this::goBack);
        btnSubmit.setOnClickListener(this::onSubmitButtonClick);
    }

    private void goBack(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateData();
    }

    // check journey object, fetch if it is null and populate the views
    private void populateData() {
        int id = intentData.getInt(ID);
        Log.d(TAG, "ID=" + id);
        //if journey is null, fetch data
        if (journey == null) {
            Cursor cursor = dbHelper.getElementById(id);
            if (cursor != null) {
                cursor.moveToFirst();
                journey = new JourneyModel(
                        id,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getBlob(4)
                );
            } else {
                Toast.makeText(getApplicationContext(), "Could not load data",
                        Toast.LENGTH_SHORT).show();
            }
        }
        // populate views
        txtTitle.setText(journey.getTitle());
        txtLocation.setText(journey.getLocation());
        txtDesc.setText(journey.getDesc());
        // if imageUri is null run this block
        if (imageUri == null) {
            byte[] imgBlob = journey.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);
            imShowImage.setImageBitmap(bitmap);
        }
    }

    private void onSubmitButtonClick(View view) {
        String title = txtTitle.getText().toString();
        String desc = txtDesc.getText().toString();
        String location = "Hard Coded Location";
        byte[] imgBlob = imageViewToByte(imShowImage);
        if (validateData(title, desc, location, imgBlob)) {
            saveEditData(title, desc, location, imgBlob);
        }
    }

    // save data
    private void saveEditData(String title, String desc, String location, byte[] imageBlob) {
        int id = getIntent().getExtras().getInt(ID);
        if (dbHelper.update(
                id,
                title,
                desc,
                location,
                imageBlob
        )) {
            Toast.makeText(getApplicationContext(), "Updated Successfully",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to save",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // validate
    private boolean validateData(String title, String desc, String location, byte[] imageBlob) {
        if (title.isEmpty()) {
            txtTitle.setError("Title is required!");
            txtTitle.requestFocus();
            return false;
        }
        if (desc.isEmpty()) {
            txtDesc.setError("Description is required!");
            txtDesc.requestFocus();
            return false;
        }
        if (location.isEmpty()) {
            txtLocation.setError("Location is required!");
            txtLocation.requestFocus();
            return false;
        }
        if (imageBlob.length == 0) {
            Toast.makeText(getApplicationContext(), "Please select an image with valid format!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressLint("IntentReset")
    private void promptToChooseImage(View view) {
        // open gallery to select image
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    private void promptToAddLocation(View view) {
        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Location Permission is granted.");
            // if permission granted
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            // retrieve location
                            if (location != null) {
                                Log.i(TAG, "Location retrieved.");
                                try {
                                    // init geocoder
                                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    // get address list
                                    List<Address> addresses = geocoder.getFromLocation(
                                            location.getLatitude(), location.getLongitude(), 1
                                    );
                                    String address = addresses.get(0).getLocality();
                                    Log.i(TAG, address);
                                    txtLocation.setText(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Could not retrieve location.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null){
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    44
            );
        }
    }

    /*
     * Override onActivityResult method: it checks if this activity
     * was paused requesting any type of result from other activities
     * by matching its request code with a known request code of this activity.
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            try {
                // create a buffer from the image at given uri
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                // decode buffer into bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // set bitmap for the ImageView
                imShowImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Could not pick image!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Convert image drawable in an imageview to byte[]
     * Takes ImageView object as param
     * */
    public static byte[] imageViewToByte(ImageView image) {
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }
}