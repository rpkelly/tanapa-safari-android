package edu.clemson.tanapasafari.geofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.google.android.gms.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class GeofenceRequester
	implements
		OnAddGeofencesResultListener,
		ConnectionCallbacks,
		OnConnectionFailedListener {

	private final Activity activity;
	private PendingIntent geofencePendingIntent;
	private ArrayList<Geofence> geofences;
	private LocationClient locationClient;
	private boolean inProgress;
	
	public GeofenceRequester(Activity activityContext){
		activity = activityContext;
		
		geofencePendingIntent = null;
		locationClient = null;
		inProgress = false;
	}
	
	public void setInProgressFlag(boolean flag){
		inProgress = flag;
	}
	public boolean getInProgressFlag(){
		return inProgress;
	}
	
	public PendingIntent getRequestPendingIntent(){
		return createRequestPendingIntent();
	}
	
	public void addGeofences(List<Geofence> gfs) throws
		UnsupportedOperationException{
		
		geofences = (ArrayList<Geofence>) gfs;
		if (!inProgress){
			inProgress = true;
			requestConnection();
		} else {
			throw new UnsupportedOperationException();
		}
		
	}
	
	private void requestConnection(){
		getLocationClient().connect();
	}
	
	private GooglePlayServicesClient getLocationClient(){
		if (locationClient == null){
			locationClient = new LocationClient(activity, this, this);
		}
		return locationClient;
	}
	
	private void continueAddGeofences() {
		geofencePendingIntent = createRequestPendingIntent();
		
		locationClient.addGeofences(geofences, geofencePendingIntent, this);
	}
	
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds){
		Intent broadcastIntent = new Intent();
		String msg;
		
		// Messages of geofence IDs logged (connected or failed) 
		if (LocationStatusCodes.ERROR == statusCode){
			
		}
		requestDisconnection();
	}
	
	private void requestDisconnection(){
		inProgress = false;
		getLocationClient().disconnect();
	}
	
	public void onConnected(Bundle arg0){
		// log connection
		continueAddGeofences();
	}
	
	public void onDisconnected(){
		inProgress = false;
		// log disconnection
		locationClient = null;
	}
	
	private PendingIntent createRequestPendingIntent(){
		if (null != geofencePendingIntent){
			return geofencePendingIntent;
		} else {
			Intent intent = new Intent(activity, ReceiveTransitionsIntentService.class);			
			return PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		
	}
	
	public void onConnectionFailed(ConnectionResult connectionResult) {
		inProgress = false;
		
		if (connectionResult.hasResolution()){
			try{
				//start activity that tries to resolve error...
				connectionResult.startResolutionForResult(activity, 9000);
			} catch (SendIntentException e){
				e.printStackTrace();
			}			
		}		
	}

}
