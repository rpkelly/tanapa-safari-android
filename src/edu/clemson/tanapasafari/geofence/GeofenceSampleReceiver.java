package edu.clemson.tanapasafari.geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

// Receives updates from connection listeners and geofence transition service
public class GeofenceSampleReceiver extends BroadcastReceiver{

	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		
		if (TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_ERROR)){
			handleGeofenceError(context,intent);
		} else if (
			TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_ADDED) ||
			TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCES_REMOVED)) {
				// Geofence added or removed, no change
		} else if(TextUtils.equals(action, GeofenceUtils.ACTION_GEOFENCE_TRANSITION)){
			handleGeofenceTransition(context, intent);
		} else {
			// The intent contained an invalid action. error log?
		}
	}
	
	public void handleGeofenceTransition(Context context, Intent intent){
		// Update UI based on geofence transition(entrance or exit)
		
	}
	
	public void handleGeofenceError(Context context, Intent intent){
		String msg = intent.getStringExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS);
		Log.e(GeofenceUtils.APPTAG, msg);
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
}
