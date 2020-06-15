package com.example.addproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androdocs.httprequest.HttpRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Barometer extends Fragment implements SensorEventListener {
    public static final String TAG = "Barometer";
    public SensorManager sensorManager;
    public float pressure;
    public float pressure_min;
    public float pressure_max;
    public String pressure_reported;
    private Context context;
    private Sensor pressureSensor;
    private Button button;

    private DatabaseReference mDatabase;

    TextView minValue;
    TextView maxValue;
    TextView currentValue;
    TextView reportedValue;

    private boolean optionsMBar;
    private String suffix;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barometer_layout, container, false);
        activateBarometer();

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        optionsMBar = pref.getBoolean("mBar", true);
        changePrefixSuffix();

        minValue = view.findViewById(R.id.minPressureValue);
        maxValue = view.findViewById(R.id.maxPressureValue);
        currentValue = view.findViewById(R.id.press_value);
        reportedValue = view.findViewById(R.id.reportedPressureValue);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        button = (Button) view.findViewById(R.id.save_pressure);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(pressure));
                Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_LONG).show();
                savePressure();
            }
        });
        new Barometer.weatherTask().execute();

        return view;
    }

    private void changePrefixSuffix() {
        if(optionsMBar){
            suffix = " mBar";
        } else {
            suffix = " Pa";
        }

    }

    public void savePressure(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy hhmmss");
        String date = dateFormat.format(calendar.getTime());
        mDatabase.child("Data").child("Pressure").child(date).child("Min").setValue(pressure_min);
        mDatabase.child("Data").child("Pressure").child(date).child("Max").setValue(pressure_max);
        mDatabase.child("Data").child("Pressure").child(date).child("Current").setValue(pressure);
    }
    public void activateBarometer(){
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(context, "No Pressure Sensor", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {

            pressure = event.values[0];
            float temp_rep_value = 0;
            if(!optionsMBar){
                if(pressure_reported != null){
                    temp_rep_value = (Float.valueOf(pressure_reported)) * 100;
                }
                pressure = pressure * 100;
            } else {
                if(pressure_reported != null){
                    temp_rep_value = Float.valueOf(pressure_reported);
                }

            }


            reportedValue.setText(String.valueOf(temp_rep_value) + suffix);
            if(pressure_min == 0 && pressure_max == 0){
                pressure_min = pressure;
                pressure_max = pressure;
                minValue.setText(String.valueOf(pressure) + suffix);
                maxValue.setText(String.valueOf(pressure) + suffix);
            }
            currentValue.setText(String.valueOf(pressure) + suffix);
        }
        if(pressure >= pressure_max){
            pressure_max = pressure;
            maxValue.setText(String.valueOf(pressure) + suffix);
        }
        if (pressure <= pressure_min){
            pressure_min = pressure;
            minValue.setText(String.valueOf(pressure) + suffix);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getPressure(){
        return pressure;
    }
    public void closeBarometer(){
        unregisterAll();
    }

    private void unregisterAll(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    String CITY = "Singapore, Singapore";
    String API = "09d8db7607046ea8681107f1455102e5";
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
//                String temp = main.getString("temp") + "°C";
//                String pressure = main.getString("pressure");
                pressure_reported = main.getString("pressure");
                Log.d(TAG, "PRESSURE IS: " + pressure_reported);
//                String humidity = main.getString("humidity");

//                Long sunrise = sys.getLong("sunrise");
//                Long sunset = sys.getLong("sunset");
//                String windSpeed = wind.getString("speed");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");
                Log.d(TAG, updatedAtText);


            } catch (JSONException e) {
                ;
            }

        }
    }
}
