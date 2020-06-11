package com.example.addproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

public class Hygrometer implements SensorEventListener {
    public static final String TAG = "Hygrometer";
    public SensorManager sensorManager;
    public float humidity;
    private Context context;

    public Hygrometer(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }


    public void activateHygrometer(){
        Sensor humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        if(humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(context, "No Relative Humidity Sensor", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            humidity = event.values[0];
            Log.d(TAG, String.valueOf(humidity));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getHumidity(){
        return humidity;
    }
    public void closeHygrometer(){
        unregisterAll();
    }

    private void unregisterAll(){
        sensorManager.unregisterListener(this);
    }

}
