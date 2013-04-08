package ua.lsoft.videorecorder.core;

import ua.lsoft.videorecorder.listeners.ListenerGPS;
import ua.lsoft.videorecorder.listeners.ListenerSensor;
import ua.lsoft.videorecorder.listeners.ReceiverBattary;
import ua.lsoft.videorecorder.listeners.ReceiverPower;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class DataPicking {

	private ListenerSensor lSensor;
	private ListenerGPS lGPS;
	private ReceiverBattary rBattary;
	private ReceiverPower rPower;
	
	private Context context;

	public DataPicking(Context context) {
		
		this.context = context;
		
		lSensor = new ListenerSensor(context);
		
		lGPS = new ListenerGPS(context);

		rBattary = new ReceiverBattary();
		IntentFilter rBattaryFilter = new IntentFilter();
		rBattaryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(rBattary, rBattaryFilter);

		rPower = new ReceiverPower();
		IntentFilter rPowerFilter = new IntentFilter();
		rPowerFilter.addAction(Intent.ACTION_POWER_CONNECTED);
		rPowerFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		rPowerFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(rPower, rPowerFilter);
	}
	
	public void release() {
		
		lSensor.release();
		lGPS.release();
		
		context.unregisterReceiver(rBattary);
		context.unregisterReceiver(rPower);
	}

	
	public ListenerSensor getlSensor() {
		return lSensor;
	}

	public ListenerGPS getlGPS() {
		return lGPS;
	}

	public ReceiverBattary getrBattary() {
		return rBattary;
	}

	public ReceiverPower getrPower() {
		return rPower;
	}
}
