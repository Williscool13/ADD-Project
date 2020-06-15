package com.example.addproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class SensorsActivity extends AppCompatActivity {
    private static final String TAG = "Sensor Activity";
    private Toolbar toolbar;
    private SectionsStatePagerAdapter mSectionsStatePageAdapter;
    private ViewPager mViewPager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);
        setTitle(TAG);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //Bottom Navigation Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.data_collection:
                        Intent menuIntent = new Intent(SensorsActivity.this, MainActivity.class);
                        startActivity(menuIntent);
                        break;
                    case R.id.individual_sensors:
                        break;
                    case R.id.location:
                        Intent locationIntent = new Intent(SensorsActivity.this, LocationActivity.class);
                        startActivity(locationIntent);
                        break;
                    case R.id.settings:
                        break;
                }
                return false;
            }
        });

        //Top Bar (tabs)
        mSectionsStatePageAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container2);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_ac_unit_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_bubble_chart_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_compare_arrows_24);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Thermometer(), "Thermometer");
        adapter.addFragment(new Hygrometer(), "Hygrometer");
        adapter.addFragment(new Barometer(), "Barometer");

        viewPager.setAdapter(adapter);
    }
    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

}
