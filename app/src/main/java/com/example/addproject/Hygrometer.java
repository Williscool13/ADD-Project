package com.example.addproject;

import android.content.Context;
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

public class Hygrometer extends Fragment implements SensorEventListener {
    public static final String TAG = "Hygrometer";
    public SensorManager sensorManager;
    public float humidity;
    public float humidity_min;
    public float humidity_max;
    public String humidity_reported;
    private Context context;
    private Button button;
    private Sensor humiditySensor;

    private DatabaseReference mDatabase;

    TextView minValue;
    TextView maxValue;
    TextView currentValue;
    TextView reportedValue;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hygrometer_layout, container, false);
        activateHygrometer();


        minValue = view.findViewById(R.id.minHumidityValue);
        maxValue = view.findViewById(R.id.maxHumidityValue);
        currentValue = view.findViewById(R.id.humi_value);
        reportedValue = view.findViewById(R.id.reportedHumidityValue);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        button = (Button) view.findViewById(R.id.save_humidity);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(humidity));
                Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_LONG).show();
                saveHumidmity();
            }
        });

        new Hygrometer.weatherTask().execute();

        return view;
    }

    public void saveHumidmity(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy hhmmss");
        String date = dateFormat.format(calendar.getTime());
        mDatabase.child("Data").child("Humidity").child(date).child("Min").setValue(humidity_min);
        mDatabase.child("Data").child("Humidity").child(date).child("Max").setValue(humidity_max);
        mDatabase.child("Data").child("Humidity").child(date).child("Current").setValue(humidity);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            humidity = event.values[0];
        }

        if (event.values.length > 0) {
            reportedValue.setText(humidity_reported);
            humidity = event.values[0];
            if(humidity_min == 0 && humidity_max == 0){
                humidity_min = humidity;
                humidity_max = humidity;
                minValue.setText(String.valueOf(humidity));
                maxValue.setText(String.valueOf(humidity));
            }
            currentValue.setText(String.valueOf(humidity));
        }
        if(humidity >= humidity_max){
            humidity_max = humidity;
            maxValue.setText(String.valueOf(humidity));
        }
        if (humidity <= humidity_min){
            humidity_min = humidity;
            minValue.setText(String.valueOf(humidity));
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public float getHumidity(){
        return humidity;
    }
    public void activateHygrometer(){
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if(humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(context, "No Relative Humidity Sensor", Toast.LENGTH_LONG).show();
        }
    }
    public void closeHygrometer(){
        unregisterAll();
    }
    private void unregisterAll(){
        sensorManager.unregisterListener(this);
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
//                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
//                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                humidity_reported = main.getString("humidity");

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
