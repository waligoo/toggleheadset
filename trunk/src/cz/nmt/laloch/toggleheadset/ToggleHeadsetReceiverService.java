package cz.nmt.laloch.toggleheadset;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ToggleHeadsetReceiverService extends Service {
	private static final String TAG = "ToggleHeadsetReceiverService";

	private ToggleHeadsetBroadcastReceiver receiver;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		receiver = new ToggleHeadsetBroadcastReceiver();
		getApplicationContext().registerReceiver(receiver,
				new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		if (receiver != null) {
			getApplicationContext().unregisterReceiver(receiver);
			receiver = null;
		}
		
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
