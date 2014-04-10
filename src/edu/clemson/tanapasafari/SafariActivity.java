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
import edu.clemson.tanapasafari.model.PointOfInterest;
import edu.clemson.tanapasafari.model.SafariListItem;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.SafariListItem;
import edu.clemson.tanapasafari.model.SafariWayPoint;
import edu.clemson.tanapasafari.model.SafariWithMediaUrls;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import edu.clemson.tanapasafari.geofence.GeofenceRequester;
import edu.clemson.tanapasafari.geofence.GeofenceSampleReceiver;
import edu.clemson.tanapasafari.geofence.SimpleGeofence;

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
	
	private void loadSafari() {
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.safari_headerImageView), getString(R.string.base_url) + safari.getHeaderMediaUrl());
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.safari_footerImageView), getString(R.string.base_url) + safari.getFooterMediaUrl());
		((TextView)this.findViewById(R.id.safari_descriptionTextView)).setText(safari.getDescription());
	}

	public void launchGuide(View view){
		//Download all waypoints in case data connectivity is lost
		WebServiceClientHelper.doGet(getString(R.string.base_url) + "/safaridetails.php?id=" + safari.getId(), new ResponseHandler(){
			@Override
			public void onResponse(Response r) {
				try {
					JSONObject jsonResponse = new JSONObject(r.getData());
					Log.d(Constants.LOGGING_TAG, jsonResponse.toString());
					JSONArray jsonWayPoints  = jsonResponse.getJSONArray("results");
					TanapaDbHelper.getInstance(getBaseContext()).clearWayPoints();
					for (int i = 0; i < jsonWayPoints.length(); i++) {
						JSONObject currentObject = (JSONObject) jsonWayPoints.get(i);
						SafariWayPoint wayPoint = new SafariWayPoint(currentObject);
						TanapaDbHelper.getInstance(getBaseContext()).saveWayPoint(wayPoint);						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});
		
		//Launch Map
		Intent guideActivityIntent = new Intent(this, GuideActivity.class);
		guideActivityIntent.putExtra("safariId", safari.getId());
		startActivity(guideActivityIntent);
	}
}
