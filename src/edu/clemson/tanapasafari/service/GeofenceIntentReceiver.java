package edu.clemson.tanapasafari.service;

import android.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GeofenceIntentReceiver extends BroadcastReceiver{

	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		boolean entering = intent.getBooleanExtra(key, false);
		
		if (entering){
			Log.d(getClass().getSimpleName(), "entering");
		} else {
			Log.d(getClass().getSimpleName(), "exiting");
		}
		
		NotificationManager notificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, null, 0);
		
		NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
				.setContentTitle("Point of interest!")
				.setContentText("You are near a point of interest!")
				.setSmallIcon(R.drawable.btn_star)
				.setContentIntent(pendingIntent);
		
		notificationManager.notify(8675, notification.build());		
	}

}
