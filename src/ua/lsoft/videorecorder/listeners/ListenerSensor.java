package ua.lsoft.videorecorder.listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ListenerSensor implements SensorEventListener {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private float x, y, z;
	private long timestamp;

	public ListenerSensor(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void release() {
		sensorManager.unregisterListener(this, accelerometer);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		
		timestamp = event.timestamp;
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	
}
