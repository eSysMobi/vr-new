package ua.lsoft.videorecorder.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class ReceiverPower extends BroadcastReceiver {

	private boolean connected;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			Log.i("ReceiverPower", "Power connected");
			connected = true;
		}

		if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			Log.i("ReceiverPower", "Power disconnected");
			connected = false;
		}
		
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			Log.i("ReceiverPower", "Power changed");
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			connected = (status == BatteryManager.BATTERY_STATUS_CHARGING);
		}
		
		Log.i("ReceiverPower", intent.getAction()+"!");
	}

	public boolean isConnected() {
		return connected;
	}
	
}
