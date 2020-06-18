package com.example.addproject;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androdocs.httprequest.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataCollection extends Fragment implements SensorEventListener{
    private static final String TAG = "Data Collection";
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor pressureSensor;
    private Sensor humiditySensor;

    private float temperature;
    private float pressure;
    private float humidity;
    private float latitude;
    private float longitude;
    private String weatherInfo;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2*1000;


    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Location currentLocation;
    private GoogleMap mMap;


    private DatabaseReference mDatabase;


    TextView temp;
    TextView pres;
    TextView humi;
    TextView lat;
    TextView lon;
    TextView weather;

    String CITY = "Singapore, Singapore";
    String API = "09d8db7607046ea8681107f1455102e5";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_collection, container, false);
        getActivity().setTitle(TAG);

        activateSensors();
        if(isServicesOk()){
            getLocationPermission();
            getDeviceLocation();
        } else {
            Toast.makeText(getActivity(), "Unable to reach Google Maps API", Toast.LENGTH_SHORT).show();
        }

        temp = view.findViewById(R.id.temperature_value);
        pres = view.findViewById(R.id.pressure_value);
        humi = view.findViewById(R.id.humidity_value);
        lat = view.findViewById(R.id.latitude_value);
        lon = view.findViewById(R.id.longitude_value);
        weather = view.findViewById(R.id.weather_info);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        Button btn = view.findViewById(R.id.data_collect_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
                Log.d(TAG, "Data has been logged in firebase");
                Toast.makeText(getActivity(), "Data has been logged", Toast.LENGTH_LONG).show();
            }
        });

        new weatherTask().execute();
        return view;


    }

    private void updateDatabase(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy hhmmss");
        String date = dateFormat.format(calendar.getTime());
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        boolean optionsCelsius = pref.getBoolean("Celsius", true);
        boolean optionsMBar = pref.getBoolean("mBar", true);
        boolean optionsRelative = pref.getBoolean("Relative", true);

        if(optionsCelsius){
            mDatabase.child("Data").child("Main").child(date).child("Temperature").setValue(temperature + "°C");
        } else {
            mDatabase.child("Data").child("Main").child(date).child("Temperature").setValue(temperature + "°F");
        }
        if(optionsMBar){
            mDatabase.child("Data").child("Main").child(date).child("Pressure").setValue(pressure + "mBar");
        } else {
            mDatabase.child("Data").child("Main").child(date).child("Pressure").setValue(pressure + "Pa");
        }
        if(optionsRelative){
            mDatabase.child("Data").child("Main").child(date).child("Humidity").setValue(humidity + "% (Relative)");
        } else {
            mDatabase.child("Data").child("Main").child(date).child("Humidity").setValue(humidity + "% (Absolute)");
        }

        mDatabase.child("Data").child("Main").child(date).child("Latitude").setValue(latitude);
        mDatabase.child("Data").child("Main").child(date).child("Longitude").setValue(longitude);
        mDatabase.child("Data").child("Main").child(date).child("Weather").setValue(toTitleCase(weatherInfo));
    }
    //Value Maintenance Business
    private void setLatLong() {
        latitude = (float) currentLocation.getLatitude();
        longitude = (float) currentLocation.getLongitude();
    }
    private void updateValues(){
        setLatLong();
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        boolean optionsCelsius = pref.getBoolean("Celsius", true);
        boolean optionsMBar = pref.getBoolean("mBar", true);
        boolean optionsRelative = pref.getBoolean("Relative", true);

        if(optionsCelsius){
            temp.setText(String.valueOf(temperature) + "°C");
        } else {
            temp.setText(String.valueOf(temperature) + "°F");
        }
        if(optionsMBar){
            pres.setText(String.valueOf(pressure) + "mBar");
        } else {
            pres.setText(String.valueOf(pressure) + "Pa");
        }
        if(optionsRelative){
            humi.setText(String.valueOf(humidity) + "% (Relative)");
        } else {
            humi.setText(String.valueOf(humidity) + "% (Absolute)");
        }

        lat.setText(String.valueOf(latitude));
        lon.setText(String.valueOf(longitude));
        weather.setText(toTitleCase(weatherInfo));
    }
    //Sensor Business
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        activateSensors();
        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                if(getActivity() != null){
                    getDeviceLocation();
                }
                if(currentLocation != null && getActivity() != null){
                    updateValues();
                }
                Log.d(TAG,"Sensors and Location Are Working!");
                handler.postDelayed(runnable, delay);
            }
        }, delay);
    }
    private void activateSensors(){
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(getActivity(), "No Ambient Temperature Sensor", Toast.LENGTH_LONG).show();
        }
        if(pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(getActivity(), "No Pressure Sensor", Toast.LENGTH_LONG).show();
        }
        if(humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(getActivity(), "No Humidity Sensor", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)){
            if (event.values.length > 0) {
                temperature = event.values[0];
            }
        }
        if(event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)){
            if (event.values.length > 0) {
                pressure = event.values[0];
            }
        }
        if(event.sensor == sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)){
            if (event.values.length > 0) {
                humidity = event.values[0];
            }
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    //Location Business
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionsGranted) {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Location Found!");
                            currentLocation = (Location) task.getResult();

                        } else {
                            Log.d(TAG, "Current location is Null");
                            Toast.makeText(getActivity(), "unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }
    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
        if(available == ConnectionResult.SUCCESS){
            //all is good
            Log.d(TAG, "isServicesOk: Google Play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error occured but can be resolved
            Log.d(TAG, "isServicesOk: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }


    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                weatherInfo = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");
                Log.d(TAG, updatedAtText);


            } catch (JSONException e) {
            }

        }
    }
}
