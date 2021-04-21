package com.example.videoplayertest.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.videoplayertest.R;
import com.example.videoplayertest.ui.fragments.DashboardFragment;
import com.example.videoplayertest.ui.fragments.NotificationsFragment;
import com.example.videoplayertest.ui.fragments.VideoFromDeviceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import Adapter.VideoAdapter;
import Model.VideoModel;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new VideoFromDeviceFragment());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new VideoFromDeviceFragment();
                //Toast.makeText(getApplicationContext(),
                // "Пора покормить кота!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_notifications:
                fragment = new NotificationsFragment();
                //Toast.makeText(getApplicationContext(),
                //        "Пора покормить кота!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                //Toast.makeText(getApplicationContext(),
                //        "Пора покормить кота!!!", Toast.LENGTH_SHORT).show();
                break;
        }
        return loadFragment(fragment);
    }
}