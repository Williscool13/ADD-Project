package com.example.addproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Settings extends AppCompatActivity {
    public static final String TAG = "Settings";

    private Toolbar toolbar;

    SwitchCompat sw;
    SwitchCompat sw1;
    SwitchCompat sw2;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Settings");



        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref.edit();

        sw = (SwitchCompat) findViewById(R.id.temperatureSwitch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Celsius", true);
                    sw.setChecked(true);
                } else {
                    sw.setChecked(false);
                    editor.putBoolean("Celsius", false);
                    Log.d(TAG, "fahrenheit enabled");
                }
                editor.apply();
            }
        });
        sw1 = (SwitchCompat) findViewById(R.id.humiditySwitch);
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Relative", true);
                } else {
                    editor.putBoolean("Relative", false);
                }
                editor.apply();
            }
        });
        sw2 = (SwitchCompat) findViewById(R.id.pressureSwitch);
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("mBar", true);
                } else {
                    editor.putBoolean("mBar", false);
                }
                editor.apply();
            }
        });

        checkChecked();












        //Bottom Nav Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.data_collection:
                        Intent dataCollectionIntent = new Intent(Settings.this, MainActivity.class);
                        startActivity(dataCollectionIntent);
                        break;
                    case R.id.individual_sensors:
                        Intent sensorIntent = new Intent(Settings.this, SensorsActivity.class);
                        startActivity(sensorIntent);
                        break;
                    case R.id.location:
                        Intent locationIntent = new Intent(Settings.this, LocationActivity.class);
                        startActivity(locationIntent);
                        break;
                    case R.id.settings:
                        break;


                }
                return false;
            }
        });
    }

    private void checkChecked() {
        if(pref.getBoolean("Celsius", true)){
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }
        if(pref.getBoolean("Relative", true)){
            sw1.setChecked(true);
        } else {
            sw1.setChecked(false);
        }
        if(pref.getBoolean("mBar", true)){
            sw2.setChecked(true);
        } else {
            sw2.setChecked(false);
        }
    }
}
