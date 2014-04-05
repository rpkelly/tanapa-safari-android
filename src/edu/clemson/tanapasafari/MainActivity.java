package edu.clemson.tanapasafari;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.model.SafariListItem;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;

public class MainActivity extends Activity {

	private LinearLayout currentRowLayout = null; 
	
	private final OnClickListener safariOnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Log.d(Constants.LOGGING_TAG, "ID of safari image view clicked: " + Integer.toString(v.getId()));
			openSafariActivity(v.getId());
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WebServiceClientHelper.doGet(getString(R.string.base_url) + "/safari.php", new ResponseHandler(){

			@Override
			public void onResponse(Response r) {
				List<SafariListItem> safaris = new ArrayList<SafariListItem>();
				try {
					JSONObject jsonResponse = new JSONObject(r.getData());
					Log.d(Constants.LOGGING_TAG, jsonResponse.toString());
					JSONArray jsonSafaris  = jsonResponse.getJSONArray("results");
					for (int i = 0; i < jsonSafaris.length(); i++) {
						JSONObject currentObject = (JSONObject) jsonSafaris.get(i);
						SafariListItem safari = new SafariListItem(currentObject);
						safaris.add(safari);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				displaySafaris(safaris);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	private void displaySafaris(List<SafariListItem> safaris) {
		for (SafariListItem safari : safaris) {
			displaySafari(safari);
		}
	}
	
	private void displaySafari(SafariListItem safari) {
		boolean rowFilled = true;
		Log.d(Constants.LOGGING_TAG, safari.getTileMediaUrl());
		String tileUrl = getString(R.string.base_url) + safari.getTileMediaUrl();
		Log.d(Constants.LOGGING_TAG, tileUrl);
		
		ImageView imageView = new ImageView(getBaseContext());
		imageView.setId(safari.getId());
		imageView.setOnClickListener(safariOnClickListener);
		
		if (currentRowLayout == null) {
			// If currentRowLayout is null, create a new horizontal linear layout.
			currentRowLayout = new LinearLayout(getBaseContext());
			currentRowLayout.setOrientation(LinearLayout.HORIZONTAL);
			((LinearLayout) this.findViewById(R.id.container)).addView(currentRowLayout);
			rowFilled = false;
		} 
		
		currentRowLayout.addView(imageView);
		
		if (rowFilled) {
			currentRowLayout = null;
		}
		
		UrlImageViewHelper.setUrlDrawable(imageView, tileUrl);
	}

	private final void openSafariActivity(int safariId) {
		Intent safariActivityIntent = new Intent(this, SafariActivity.class);
		safariActivityIntent.putExtra("safariId", safariId);
		startActivity(safariActivityIntent);
	}

	protected void onResume()
	{
	   super.onResume();

	   int googlePlayServicesAvail = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	   if( googlePlayServicesAvail != ConnectionResult.SUCCESS){
		   GooglePlayServicesUtil.getErrorDialog(googlePlayServicesAvail, this, 1122).show();
	   }
	}
}

