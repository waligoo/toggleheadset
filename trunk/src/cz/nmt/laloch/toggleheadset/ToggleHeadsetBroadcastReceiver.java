package cz.nmt.laloch.toggleheadset;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ToggleHeadsetBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "ToggleHeadsetBroadcastReceiver";

	public static final String TOGGLEHEADSET_ACTION = "cz.nmt.laloch.toggleheadset.headsetstatereceiver.toggleheadset";

	public static boolean switching = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive intent=" + intent);

		String action = intent.getAction();
		
        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
        	try { 
	    		ToggleHeadsetAppWidgetProvider.state = intent.getIntExtra("state", 0);
	        	AppWidgetManager gm = AppWidgetManager.getInstance(context);
	            int[] appWidgetIds = gm.getAppWidgetIds(
	            		new ComponentName(context, ToggleHeadsetAppWidgetProvider.class));
	            final int N = appWidgetIds.length;
	            for (int i=0; i<N; i++) {
	                ToggleHeadsetAppWidgetProvider.updateAppWidget(context, gm,	appWidgetIds[i],
	                		false);
	            }
        	} finally {
        		switching = false;
        	}
        } else if (action.equals(TOGGLEHEADSET_ACTION)) {
        	if (!switching) {
        	switching = true;
	        	try {
		        	AppWidgetManager gm = AppWidgetManager.getInstance(context);
		            int[] appWidgetIds = gm.getAppWidgetIds(
		            		new ComponentName(context, ToggleHeadsetAppWidgetProvider.class));
		            final int N = appWidgetIds.length;
		            for (int i=0; i<N; i++) {
		                ToggleHeadsetAppWidgetProvider.updateAppWidget(context, gm,	appWidgetIds[i],
		                		true);
		            }
		        	ToggleHeadsetAppWidgetProvider.toggleHeadset(context);
				} catch (Exception e) {
					switching = false;
				}
        	}
        }
	}
}
