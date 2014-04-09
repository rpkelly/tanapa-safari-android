package edu.clemson.tanapasafari.geofence;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import edu.clemson.tanapasafari.SafariActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

	// receives transition events in intent containing transition type and geofence id
public class ReceiveTransitionsIntentService extends IntentService{
	
	public ReceiveTransitionsIntentService(){
		super("ReceiveTransitionsIntentService");
	}
	
	protected void onHandleIntent(Intent intent){
		Intent broadcastIntent = new Intent();
		
		broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
		
		if (LocationClient.hasError(intent)){
			Log.e(GeofenceUtils.APPTAG, "LocationClient Error");
			// Set the action and error message for the broadcast intent
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, "LocationClient Error");

            // Broadcast the error *locally* to other components in this app
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);			
		} else {
			int transition = LocationClient.getGeofenceTransition(intent);
			
			// Test that a valid transition was reported
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {

                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size() ; index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                }
                String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,geofenceIds);
                String transitionType = getTransitionString(transition);

                sendNotification(transitionType, ids);

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(GeofenceUtils.APPTAG, "invalid transition reported");
            }			
		}
	}
	
	// sends notification when transition is detected `
    private void sendNotification(String transitionType, String ids) {

        
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";

            default:
                return "unknown";
        }

    }
}
