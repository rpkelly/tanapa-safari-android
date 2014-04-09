package edu.clemson.tanapasafari;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.model.SafariListItem;
import edu.clemson.tanapasafari.model.SafariWithMediaUrls;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import edu.clemson.tanapasafari.geofence.GeofenceRequester;
import edu.clemson.tanapasafari.geofence.GeofenceSampleReceiver;
import edu.clemson.tanapasafari.geofence.SimpleGeofence;
import edu.clemson.tanapasafari.geofence.PointOfInterest;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.drm.DrmManagerClient.OnEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class SafariActivity extends Activity {	
	private SafariWithMediaUrls safari;
	private int safariId;
	
	private List<Geofence> geofences;
	private List<PointOfInterest> pois;
	private GeofenceRequester geofenceRequester;
	private GeofenceSampleReceiver broadcastReceiver;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safari);
		
		//getGeofences();
		//geofenceRequester = new GeofenceRequester(this);
		//registerGeofences();
		
		Intent intent = getIntent();
		safariId = intent.getIntExtra("safariId", -1);
		String url = getString(R.string.base_url) + "/safari.php?id=" + safariId;
		WebServiceClientHelper.doGet(url, new ResponseHandler() {

			public void onResponse(Response r) {
				try {
					JSONObject jsonResponse = new JSONObject(r.getData());
					safari = new SafariWithMediaUrls(jsonResponse);
					loadSafari();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.safari, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	/*
	public void getGeofences(){
		WebServiceClientHelper.doGet(getString(R.string.base_url) + "/poi.php", new ResponseHandler(){
			// Populate waypoint list from server
			public void onResponse(Response r) {
				pois = new ArrayList<PointOfInterest>();
				try {
					JSONObject jsonResponse = new JSONObject(r.getData());
					Log.d(Constants.LOGGING_TAG, jsonResponse.toString());
					JSONArray jsonWaypoints  = jsonResponse.getJSONArray("results");
					for (int i = 0; i < jsonWaypoints.length(); i++) {
						JSONObject currentObject = (JSONObject) jsonWaypoints.get(i);
						PointOfInterest waypoint = new PointOfInterest(currentObject);
						pois.add(waypoint);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}			
		});
		geofences = new ArrayList<Geofence>();
		// Populate list of geofences from waypoint list
		for (int i = 0; i < pois.size(); i++){
			PointOfInterest current = pois.get(i);
			if (current.getSafariId() == safariId){ // Check that waypoint is for this safari
				SimpleGeofence simplegf = new SimpleGeofence(
						Integer.toString(current.getId()),
						current.getLatitude(),
						current.getLongitude(),
						(float) current.getRadius(),
						-1,1);
				Geofence geofence = simplegf.toGeofence();
				geofences.add(geofence);
			}	
		}		
	}
	
	public void registerGeofences(){

		// Start request, fails if request in progress. 
		try{
			geofenceRequester.addGeofences(geofences);
		} catch (UnsupportedOperationException e){
			Toast.makeText(this, "Can't add geofences, previous request hasn't finished", Toast.LENGTH_LONG).show();
		}
	}
	
	*/
	private void loadSafari() {
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.safari_headerImageView), getString(R.string.base_url) + safari.getHeaderMediaUrl());
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.safari_footerImageView), getString(R.string.base_url) + safari.getFooterMediaUrl());
		((TextView)this.findViewById(R.id.safari_descriptionTextView)).setText(safari.getDescription());
	}

}
