package com.example.myjourneyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjourneyjournal.db.DbHelper;
import com.example.myjourneyjournal.model.JourneyModel;

public class JourneyDetail extends AppCompatActivity {

    public static final String TAG = JourneyDetail.class.getSimpleName();
    public static final String ID = "Id";

    private ImageView imJourney;
    private TextView tvTitle, tvLocation, tvDesc;


    DbHelper dbHelper;
    JourneyModel journey;
    public static Intent getIntent(Context context, int id){
        Intent intent = new Intent(context, JourneyDetail.class);
        intent.putExtra(ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_detail);

        imJourney = findViewById(R.id.imvJourney);
        tvTitle = findViewById(R.id.txvTitle);
        tvLocation = findViewById(R.id.txvLocation);
        tvDesc = findViewById(R.id.txvDesc);
        ImageButton ibBack = findViewById(R.id.btn_back);
        Button btnBack = findViewById(R.id.btnBack);
        dbHelper = new DbHelper(this);

        btnBack.setOnClickListener(this::goBack);
        ibBack.setOnClickListener(this::goBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupData();
    }

    private void setupData() {
        int id = getIntent().getExtras().getInt(ID);
        Log.d(TAG, id+"");
        Cursor cursor = dbHelper.getElementById(id);
        if (cursor != null){
            cursor.moveToFirst();
            journey = new JourneyModel(
                    id,
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4)
            );
        }
        if (journey != null){
            tvTitle.setText(journey.getTitle());
            tvLocation.setText(journey.getLocation());
            tvDesc.setText(journey.getDesc());

            byte[] imgBlob = journey.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);
            imJourney.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(getApplicationContext(), "Could not load data",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goBack(View view) {
        finish();
    }
}