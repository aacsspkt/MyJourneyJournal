package com.example.myjourneyjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class About extends AppCompatActivity {

    public static Intent getIntent(Context context){
        return new Intent(context, About.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // top navigation and drawer
        NavigationView navigationView = findViewById(R.id.navigation);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this::onNavItemSelected);

        // drawer menu click listener
        findViewById(R.id.drawer_menu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
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