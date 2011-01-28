package cz.nmt.laloch.toggleheadset;
/*
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class HeadsetWidget extends AppWidgetProvider {

	private static String TAG = "HeadsetWidget";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive action: " + intent.getAction());
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, HeadsetStateReceiver.class));
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		context.startService(new Intent(context, HeadsetStateReceiver.class));
	};
	
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
		context.stopService(new Intent(context, HeadsetStateReceiver.class));
		super.onDisabled(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
		context.stopService(new Intent(context, HeadsetStateReceiver.class));
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Log.d(TAG, "finalize");
		super.finalize();
	}
	
	public static class HeadsetStateReceiver extends Service {
		public static final String TOGGLEHEADSET_ACTION = "cz.nmt.laloch.toggleheadset.headsetstatereceiver.toggleheadset";

		private AudioManager audioManager;

		private int headsetState = 0;
		private String headsetName;

		
		private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Receiver.onReceive action: " + intent.getAction());
				if (intent.getAction().equals(TOGGLEHEADSET_ACTION)) {
					toggleAudio(context);
				} else {
					headsetState = intent.getIntExtra("state", 0);
					headsetName = intent.getStringExtra("name");
					updateWidget(false);
				}
			};
		};

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public void onStart(Intent intent, int startId) {
			Log.d(TAG, "Service.onStart action: " + intent.getAction());
			super.onStart(intent, startId);
			IntentFilter intentFilter = new IntentFilter(
					Intent.ACTION_HEADSET_PLUG);
			intentFilter.addAction(TOGGLEHEADSET_ACTION);
			registerReceiver(headsetReceiver, intentFilter);
			updateWidget(true);
		}

		@Override
		public void onCreate() {
			Log.d(TAG, "Service.onCreate");
			super.onCreate();
			audioManager = (AudioManager) getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE);
		};

		@Override
		public void onDestroy() {
			Log.d(TAG, "Service.onDestroy");
			unregisterReceiver(headsetReceiver);
			super.onDestroy();
		};

		private void toggleAudio(Context context) {
			boolean newState = headsetState == 0;
			if (!newState) {
				Intent intent;
				intent = new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
				context.sendBroadcast(intent);
			}

			if (newState) {
				updateAudioRoute();
			} else {
				updateWidget(true);
				delayHandler.sendEmptyMessageDelayed(0, 1000);
			}
		}

		private synchronized final void updateAudioRoute() {
			boolean newState = headsetState == 0;
			Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
			intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			intent.putExtra("state", newState ? 1 : 0);
			intent.putExtra("name", headsetName);
			getApplicationContext().sendStickyBroadcast(intent);

			audioManager
					.setRouting(AudioManager.MODE_NORMAL,
							newState ? AudioManager.ROUTE_HEADSET
									: AudioManager.ROUTE_SPEAKER,
							AudioManager.ROUTE_ALL
									& ~AudioManager.ROUTE_BLUETOOTH_A2DP);
			audioManager
					.setRouting(AudioManager.MODE_RINGTONE,
							newState ? AudioManager.ROUTE_HEADSET
									| AudioManager.ROUTE_SPEAKER
									: AudioManager.ROUTE_SPEAKER,
							AudioManager.ROUTE_ALL
									& ~AudioManager.ROUTE_BLUETOOTH_A2DP);
			audioManager.setRouting(AudioManager.MODE_IN_CALL,
					newState ? AudioManager.ROUTE_HEADSET
							: AudioManager.ROUTE_EARPIECE,
					AudioManager.ROUTE_ALL);
		}

		private void updateWidget(boolean waiting) {
			RemoteViews updateViews = new RemoteViews(getPackageName(),
					R.layout.widget_test);
			if (waiting) {
				updateViews.setImageViewResource(R.id.IconImageView,
						R.drawable.refresh);
			} else {
				updateViews.setImageViewResource(R.id.IconImageView,
						(headsetState == 0 ? R.drawable.off : R.drawable.on));
			}
			Intent intent = new Intent(
					HeadsetStateReceiver.TOGGLEHEADSET_ACTION);
			PendingIntent pendingIntent = PendingIntent
					.getBroadcast(getApplicationContext(),
							0, intent, 0);
			updateViews.setOnClickPendingIntent(R.id.IconImageView,
					pendingIntent);

			final ComponentName thisWidget = new ComponentName(this,
					HeadsetWidget.class);
			AppWidgetManager.getInstance(this).updateAppWidget(thisWidget,
					updateViews);
		}

		private final Handler delayHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateAudioRoute();
			}
		};

	}
}
*/