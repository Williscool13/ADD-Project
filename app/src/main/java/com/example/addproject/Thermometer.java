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
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Thermometer extends Fragment implements SensorEventListener {
    public static final String TAG = "Temperature";
    public SensorManager sensorManager;
    public float temperature;
    public float temperature_min;
    public float temperature_max;
    public String temperature_reported;
    private Context context;
    private Button button;
    private Sensor temperatureSensor;

    private DatabaseReference mDatabase;

    TextView minValue;
    TextView maxValue;
    TextView currentValue;
    TextView reportedValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thermometer_layout, container, false);
        activateThermometer();


        minValue = view.findViewById(R.id.minTemperatureValue);
        maxValue = view.findViewById(R.id.maxTemperatureValue);
        currentValue = view.findViewById(R.id.temp_value);
        reportedValue = view.findViewById(R.id.reportedTemperatureValue);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        button = (Button) view.findViewById(R.id.save_temperature);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(temperature));
                Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_LONG).show();
                saveTemperature();
            }
        });

        new Thermometer.weatherTask().execute();
        return view;
    }


    public void saveTemperature(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy hhmmss");
        String date = dateFormat.format(calendar.getTime());
        mDatabase.child("Data").child("Temperature").child(date).child("Min").setValue(temperature_min);
        mDatabase.child("Data").child("Temperature").child(date).child("Max").setValue(temperature_min);
        mDatabase.child("Data").child("Temperature").child(date).child("Current").setValue(temperature);
    }

    public void activateThermometer(){
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if(temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(context, "No Ambient Temperature Sensor", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            reportedValue.setText(temperature_reported);
            temperature = event.values[0];
            if(temperature_min == 0 && temperature_max == 0){
                temperature_min = temperature;
                temperature_max = temperature;
                minValue.setText(String.valueOf(temperature));
                maxValue.setText(String.valueOf(temperature));
            }
            currentValue.setText(String.valueOf(temperature));
        }
        if(temperature >= temperature_max){
            temperature_max = temperature;
            maxValue.setText(String.valueOf(temperature));
        }
        if (temperature <= temperature_min){
            temperature_min = temperature;
            minValue.setText(String.valueOf(temperature));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getTemperature(){
        return temperature;
    }
    public void closeThermometer(){
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
                temperature_reported = main.getString("temp");
                Log.d(TAG, "TEMPERATURE IS: " + temperature_reported);
//                String temp = main.getString("temp") + "°C";
//                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
//                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
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
