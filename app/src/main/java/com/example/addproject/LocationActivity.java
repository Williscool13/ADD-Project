package com.example.addproject;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.List;

public class LocationActivity extends AppCompatActivity{
    private static final String TAG = "Location";
    private Toolbar toolbar;
    private GoogleMap mMap;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private SectionsStatePagerAdapter mSectionsStatePageAdapter;
    private ViewPager mViewPager;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private String previousLongitude;

    Reading[] History;
    Map theMap = new Map();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);



        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        toolbar2.setTitle(TAG);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference().child("Data").child("Main");
        final Query dataQuery = messagesReference.limitToLast(5);
        dataQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Reading dummy = new Reading("","","","","","");
                    History = new Reading[]{dummy, dummy, dummy, dummy, dummy};
                    int i = 0;
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        String t = String.valueOf(child.child("Temperature").getValue());
                        String h = String.valueOf(child.child("Humidity").getValue());
                        String p = String.valueOf(child.child("Pressure").getValue());
                        String lt = String.valueOf(child.child("Latitude").getValue());
                        String lo = String.valueOf(child.child("Longitude").getValue());
                        String k = String.valueOf(child.getKey());

                        Array.set(History, i, new Reading(k, t,h,lt,lo,p));
                        i += 1;
                        Log.d(TAG, child.getKey());
                        Log.d(TAG, child.getValue().toString());
                    }

                    Menu navViewMenu = navigationView.getMenu();
                    MenuItem history1 = navViewMenu.findItem(R.id.history1);
                    MenuItem history2 = navViewMenu.findItem(R.id.history2);
                    MenuItem history3 = navViewMenu.findItem(R.id.history3);
                    MenuItem history4 = navViewMenu.findItem(R.id.history4);
                    MenuItem history5 = navViewMenu.findItem(R.id.history5);
                    history1.setTitle(History[0].key);
                    history2.setTitle(History[1].key);
                    history3.setTitle(History[2].key);
                    history4.setTitle(History[3].key);
                    history5.setTitle(History[4].key);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        navigationView = (NavigationView)findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.history1:
                        Toast.makeText(LocationActivity.this, "Location 1 Displaying",Toast.LENGTH_SHORT).show();
                        LatLng latLng1 = new LatLng(Double.valueOf(History[0].latitude), Double.valueOf(History[0].longitude));
                        theMap.moveCamera(latLng1, 15f);
                        theMap.clearMap();
                        theMap.addMarker(latLng1, "5 Entries Before");
                        break;
                    case R.id.history2:
                        Toast.makeText(LocationActivity.this, "Location 2 Displaying",Toast.LENGTH_SHORT).show();
                        LatLng latLng2 = new LatLng(Double.valueOf(History[1].latitude), Double.valueOf(History[1].longitude));
                        theMap.moveCamera(latLng2, 15f);
                        theMap.clearMap();
                        theMap.addMarker(latLng2, "4 Entries Before");
                        break;
                    case R.id.history3:
                        Toast.makeText(LocationActivity.this, "Location 3 Displaying",Toast.LENGTH_SHORT).show();
                        LatLng latLng3 = new LatLng(Double.valueOf(History[2].latitude), Double.valueOf(History[2].longitude));
                        theMap.moveCamera(latLng3, 15f);
                        theMap.clearMap();
                        theMap.addMarker(latLng3, "3 Entries Before");
                        break;
                    case R.id.history4:
                        Toast.makeText(LocationActivity.this, "Location 4 Displaying",Toast.LENGTH_SHORT).show();
                        LatLng latLng4 = new LatLng(Double.valueOf(History[3].latitude), Double.valueOf(History[3].longitude));
                        theMap.moveCamera(latLng4, 15f);
                        theMap.clearMap();
                        theMap.addMarker(latLng4, "2 Entries Before");
                        break;
                    case R.id.history5:
                        Toast.makeText(LocationActivity.this, "Location 5 Displaying",Toast.LENGTH_SHORT).show();
                        LatLng latLng5 = new LatLng(Double.valueOf(History[4].latitude), Double.valueOf(History[4].longitude));
                        theMap.moveCamera(latLng5, 15f);
                        theMap.clearMap();
                        theMap.addMarker(latLng5, "Previous Entry");
                        break;
                    default:
                        return true;
                }
                drawerLayout.closeDrawers();

                return true;

            }
        });




        //Fragment Manager
        mSectionsStatePageAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container3);
        setupViewPager(mViewPager);




        //Bottom Navigation Menu
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.data_collection:
                        Intent menuIntent = new Intent(LocationActivity.this, MainActivity.class);
                        startActivity(menuIntent);
                        break;
                    case R.id.individual_sensors:
                        Intent locationIntent = new Intent(LocationActivity.this, SensorsActivity.class);
                        startActivity(locationIntent);
                        break;
                    case R.id.location:

                        break;
                    case R.id.settings:
                        Intent settingsIntent = new Intent(LocationActivity.this, Settings.class);
                        startActivity(settingsIntent);
                        break;
                }
                return false;
            }
        });



        //verify api is up, and move to fragment if it is
        if(isServicesOk()){
            setViewPager(0);
        }

    }


    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //all is good
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error occured but can be resolved
            Log.d(TAG, "isServicesOk: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //View Pager
    private void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(theMap, "Map");
        viewPager.setAdapter(adapter);
    }
    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


}

