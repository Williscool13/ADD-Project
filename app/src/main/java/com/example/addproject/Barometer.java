package com.example.addproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class Barometer implements SensorEventListener {
    public static final String TAG = "Barometer";
    public SensorManager sensorManager;
    public float pressure;
    private Context context;

    public Barometer(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }


    public void activateBarometer(){
        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

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
            Log.d(TAG, String.valueOf(pressure));

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

}
