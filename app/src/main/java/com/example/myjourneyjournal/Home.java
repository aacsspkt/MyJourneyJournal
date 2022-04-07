package com.example.myjourneyjournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjourneyjournal.adapter.ItemsAdapter;
import com.example.myjourneyjournal.adapter.RVItemClickListener;
import com.example.myjourneyjournal.db.DbHelper;
import com.example.myjourneyjournal.model.JourneyModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private ArrayList<JourneyModel> journeys;
    // Database Layer
    private DbHelper dbHelper;
    //RecyclerView
    private RecyclerView recyclerView;
    private ItemsAdapter itemsAdapter;
    private RVItemClickListener delImgViewClickListener;
    private RVItemClickListener itemViewClickListener;
    private RVItemClickListener editImgViewClickListener;

    public static Intent getIntent(Context context) {
        return new Intent(context, Home.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        journeys = new ArrayList<>();
        dbHelper = new DbHelper(this);

        recyclerView = findViewById(R.id.recycler);
        FloatingActionButton btnAdd = findViewById(R.id.btn_add);

        setupListeners();
        setupAdapter();
        setupRecyclerView();

        btnAdd.setOnClickListener(this::btnAddOnClick);


        // top navigation and drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        // nav item select listener
        navigationView.setNavigationItemSelectedListener(this::onNavItemSelected);
        // drawer menu click listener
        findViewById(R.id.drawer_menu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initData() {
        String sqlQuery = "SELECT * FROM " + DbHelper.TABLE_NAME;
        Cursor cursor = dbHelper.getAll(sqlQuery);
        journeys.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                JourneyModel journey = new JourneyModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getBlob(4)
                );
                Log.d("HOME", journey.getId() + "");
                journeys.add(journey);
            }
            itemsAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupListeners() {
        itemViewClickListener = (view, position) -> startActivity(
                JourneyDetail.getIntent(getApplicationContext(), journeys.get(position).getId())
        );
        delImgViewClickListener = (view, position) -> {
            dbHelper.delete(journeys.get(position).getId());
            initData();
            itemsAdapter.notifyDataSetChanged();
        };
        editImgViewClickListener = (view, position) -> startActivity(
                EditJourney.getIntent(getApplicationContext(), journeys.get(position).getId())
        );
    }

    private void setupAdapter() {
        itemsAdapter = new ItemsAdapter(
                journeys,
                itemViewClickListener,
                delImgViewClickListener,
                editImgViewClickListener
        );
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsAdapter);
    }

    private void btnAddOnClick(View view) {
        startActivity(CreateJourney.getIntent(this));
    }

    private boolean onNavItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_menu:
                startActivity(Home.getIntent(getApplicationContext()));
                break;

            case R.id.about_menu:
                startActivity(About.getIntent(getApplicationContext()));
                break;

            case R.id.contact_menu:
                startActivity(Contact.getIntent(getApplicationContext()));
                break;

            case R.id.share_menu:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Application Link");
                startActivity(Intent.createChooser(shareIntent, "Share Via"));
                break;
        }
        return false;
    }
}