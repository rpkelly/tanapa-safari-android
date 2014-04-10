package edu.clemson.tanapasafari;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.SafariListItem;
import edu.clemson.tanapasafari.model.SafariWayPoint;
import edu.clemson.tanapasafari.model.SafariWithMediaUrls;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SafariActivity extends Activity {
	
	private SafariWithMediaUrls safari;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safari);
		
		Intent intent = getIntent();
		int safariId = intent.getIntExtra("safariId", -1);
		String url = getString(R.string.base_url) + "/safari.php?id=" + safariId;
		WebServiceClientHelper.doGet(url, new ResponseHandler() {

			@Override
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.safari, menu);
		return true;
	}

	@Override
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
					JSONArray jsonWayPoints  = jsonResponse.getJSONArray("waypoints");
					JSONArray jsonPOI = jsonResponse.getJSONArray("points_of_interest");
					Log.d(Constants.LOGGING_TAG, jsonWayPoints.toString());
					Log.d(Constants.LOGGING_TAG, jsonPOI.toString());
					TanapaDbHelper.getInstance(getBaseContext()).clearPOIs();
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
