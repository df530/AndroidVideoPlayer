package com.example.avp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.avp.R;
import com.example.avp.ui.fragments.LoginFragment;
import com.example.avp.ui.fragments.VideoByLinkFragment;
import com.example.avp.ui.fragments.VideoFromDeviceFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            case R.id.navigation_video_from_device:
                fragment = new VideoFromDeviceFragment();
                //Toast.makeText(getApplicationContext(),
                // "Пора покормить кота!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_video_by_link:
                fragment = new VideoByLinkFragment();
                //Toast.makeText(getApplicationContext(),
                //        "Пора покормить кота!!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_dashboard:
                fragment = new LoginFragment();
                //Toast.makeText(getApplicationContext(),
                //        "Пора покормить кота!!!", Toast.LENGTH_SHORT).show();
                break;
        }
        return loadFragment(fragment);
    }
}