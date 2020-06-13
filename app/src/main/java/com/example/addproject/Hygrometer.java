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

public class Hygrometer extends Fragment implements SensorEventListener {
    public static final String TAG = "Hygrometer";
    public SensorManager sensorManager;
    public float humidity = 99999999;
    private Context context;
    private Button button;
    private Sensor humiditySensor;

    public Hygrometer(Context context){
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hygrometer_layout, container, false);
        activateHygrometer();
        button = (Button) view.findViewById(R.id.get_humidity);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.valueOf(humidity));
                Toast.makeText(getActivity(), String.valueOf(humidity), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length > 0) {
            humidity = event.values[0];
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public float getHumidity(){
        return humidity;
    }
    public void activateHygrometer(){
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

}
