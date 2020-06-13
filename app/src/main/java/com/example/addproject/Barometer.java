package com.example.addproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Barometer extends Fragment implements SensorEventListener {
    public static final String TAG = "Barometer";
    public SensorManager sensorManager;
    public float pressure;
    private Context context;
    private Sensor pressureSensor;
    private Button button;

    public Barometer(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barometer_layout, container, false);
        activateBarometer();

        button = (Button) view.findViewById(R.id.get_pressure);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(pressure));
                Toast.makeText(getActivity(), String.valueOf(pressure), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void activateBarometer(){
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
}
