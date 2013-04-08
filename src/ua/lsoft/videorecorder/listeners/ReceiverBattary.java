package ua.lsoft.videorecorder.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBattary extends BroadcastReceiver {
	
	private int level;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED))
			level = intent.getIntExtra("level", 0);		
	}

	public int getLevel() {
		return level;
	}

}
