package cz.nmt.laloch.toggleheadset;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class ToggleHeadsetAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "ToggleHeadsetAppWidgetProvider";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive intent=" + intent);

		final String action = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final Bundle extras = intent.getExtras();
			final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			}
		} else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "onUpdate");

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId, false);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted");
	}
	
	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");

		final PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(
				new ComponentName(context, ToggleHeadsetReceiverService.class),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		pm.setComponentEnabledSetting(
				new ComponentName(context, ToggleHeadsetBroadcastReceiver.class),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		context.startService(new Intent(context, ToggleHeadsetReceiverService.class));
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");

		final PackageManager pm = context.getPackageManager();
		context.stopService(new Intent(context, ToggleHeadsetReceiverService.class));
		pm.setComponentEnabledSetting(
				new ComponentName(context, ToggleHeadsetBroadcastReceiver.class),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		pm.setComponentEnabledSetting(
				new ComponentName(context, ToggleHeadsetReceiverService.class),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	static int state = 0;
	
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId, boolean unknownState) {
		Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId + " state=" + state);

		int imageId;
		if (unknownState) {
			imageId = R.drawable.refresh;
		} else if (state == 0) {
			imageId = R.drawable.off;
		} else {
			imageId = R.drawable.on;
		}

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_test);
		views.setImageViewResource(R.id.IconImageView, imageId);
		Intent intent = new Intent(ToggleHeadsetBroadcastReceiver.TOGGLEHEADSET_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
						0 /* no requestCode */, intent, 0 /* no flags */);
		views.setOnClickPendingIntent(R.id.IconImageView, pendingIntent);
		
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	public static void toggleHeadset(Context context) {
		Log.d(TAG, "toggleHeadset state=" + state);

		boolean newState = state <= 0;
		if (!newState) {
			Intent intent;
			intent = new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
			context.sendBroadcast(intent);
		}

		if (newState) {
			updateAudioRoute(context);
		} else {
			Message message = new Message();
			message.obj = context;
			delayHandler.sendMessageDelayed(message, 1000);
		}
	}

	private static synchronized final void updateAudioRoute(Context context) {
		Log.d(TAG, "updateAudioRoute state=" + state);

		boolean newState = state <= 0;
		final AudioManager audioManager =
			(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		Intent intent = new Intent(Intent.ACTION_HEADSET_PLUG);
		intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		intent.putExtra("state", newState ? 1 : 0);
		intent.putExtra("name", "");
		context.getApplicationContext().sendBroadcast(intent);


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

	private static final Handler delayHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final Context context = (Context)msg.obj;
			updateAudioRoute(context);
		}
	};
	
}
