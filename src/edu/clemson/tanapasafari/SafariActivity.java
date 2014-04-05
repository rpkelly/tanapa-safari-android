package edu.clemson.tanapasafari;

import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import edu.clemson.tanapasafari.model.SafariWithMediaUrls;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.headerImageView), getString(R.string.base_url) + safari.getHeaderMediaUrl());
		UrlImageViewHelper.setUrlDrawable((ImageView) this.findViewById(R.id.footerImageView), getString(R.string.base_url) + safari.getFooterMediaUrl());
		((TextView)this.findViewById(R.id.safariDescriptionTextView)).setText(safari.getDescription());
	}
	

}
