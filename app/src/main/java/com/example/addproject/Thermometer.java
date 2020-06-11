package com.example.addproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class Thermometer implements SensorEventListener {
    public static final String TAG = "Temperature";
    public SensorManager sensorManager;
    public float temperature;
    private Context context;

    public Thermometer(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }


    public void activateThermometer(){
        Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if(temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(context, "No Ambient Temperature Sensor", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            temperature = event.values[0];
            Log.d(TAG, String.valueOf(temperature));
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

}
