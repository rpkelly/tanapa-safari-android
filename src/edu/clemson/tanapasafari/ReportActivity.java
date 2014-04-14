package edu.clemson.tanapasafari;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.ReportType;
import edu.clemson.tanapasafari.model.User;
import edu.clemson.tanapasafari.model.UserIdListener;
import edu.clemson.tanapasafari.service.GPSTracker;
import edu.clemson.tanapasafari.service.GPSTrackerSingleton;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;

public class ReportActivity extends Activity {

	private final OnClickListener saveButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			saveReport();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		String url = getString(R.string.base_url) + "/report_types.php";
		Button saveButton = (Button) findViewById(R.id.report_saveButton);
		saveButton.setOnClickListener(saveButtonOnClickListener);
		WebServiceClientHelper.doGet(url, new ResponseHandler() {

			@Override
			public void onResponse(Response r) {
				try {
					List<ReportType> reportTypes = new ArrayList<ReportType>();
					JSONObject jsonObject = new JSONObject(r.getData());
					JSONArray resultsArray = jsonObject.getJSONArray("results");
					for ( int i = 0; i < resultsArray.length(); i++ ) {
						JSONObject reportTypeJson = resultsArray.getJSONObject(i);
						ReportType reportType = new ReportType();
						reportType.setId(reportTypeJson.getInt("id"));
						reportType.setName(reportTypeJson.getString("name"));
						reportTypes.add(reportType);
					}
					setReportTypeSpinnerValues(reportTypes);
				} catch (JSONException e) {
					Log.e(Constants.LOGGING_TAG, "Error occurred while retrieving report types from web service.", e);
				}
				
			}
			
		});
		
	}
	
	private void setReportTypeSpinnerValues(List<ReportType> values) {
		Spinner reportTypeSpinner = (Spinner) this.findViewById(R.id.report_reportTypeSpinner);
		ArrayAdapter<ReportType> aa = new ArrayAdapter<ReportType>(this, android.R.layout.simple_list_item_single_choice, values);
		reportTypeSpinner.setAdapter(aa);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
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
	
	private void saveReportForUserId(int userId) {
		// Save report data to local database
		Report report = new Report();
		Spinner reportTypeSpinner = (Spinner) findViewById(R.id.report_reportTypeSpinner);
		ReportType reportType = (ReportType) reportTypeSpinner.getSelectedItem();
		EditText contentEditText = (EditText) findViewById(R.id.report_content);
		report.setReportTypeId(reportType.getId());
		report.setContent(contentEditText.getText().toString());
		report.setTime(new Date());
		report.setUserId(userId);
		GPSTracker gps = GPSTrackerSingleton.getInstance(this);
		if (gps.canGetLocation()) {
			Location location = gps.getLocation();
			Log.d(Constants.LOGGING_TAG, "Report Location: " + location.getLatitude() + ", " + location.getLongitude());
			report.setLatitude(location.getLatitude());
			report.setLongitude(location.getLongitude());
		}
		long reportId = TanapaDbHelper.getInstance(this).saveReport(report);
		Log.d(Constants.LOGGING_TAG, "Saved report locally with ID of: " + reportId);
	}
	
	private void saveReport() {
		
		User.getId(this, new UserIdListener() {

			@Override
			public void onUserId(Integer id) {
				saveReportForUserId(id);
			}
			
		});
		
	}

}
