package edu.clemson.tanapasafari.service;

import java.util.List;

import org.json.JSONObject;

import edu.clemson.tanapasafari.R;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.UserLog;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TanapaSyncService extends IntentService {

	public TanapaSyncService() {
		super("TanapaSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(Constants.LOGGING_TAG, "Starting TANAPA Sync Service processing.");
		synchronizeReports();
		synchronizeUserLogs();
		
		
	}	
	
	private void synchronizeReports() {
		List<Report> unsynchedReports = TanapaDbHelper.getInstance(null).findUnsynchronizedReports();
		Log.d(Constants.LOGGING_TAG, "Number of unsynched reports found: " + unsynchedReports.size());
		for (Report report : unsynchedReports) {
			Log.d(Constants.LOGGING_TAG, "Synchronizing report: " + report.getId());
			synchronizeReport(report);
		}
	}
	
	protected void synchronizeReport(final Report report) {
		JSONObject reportJsonObject = report.toJSON();
		Log.d(Constants.LOGGING_TAG, "Report Data: " + reportJsonObject.toString());
		String url = getString(R.string.base_url) + "/report.php";
		WebServiceClientHelper.doPost(url, reportJsonObject.toString(), new ResponseHandler() {

			@Override
			public void onResponse(Response r) {
				Log.d(Constants.LOGGING_TAG, "Report synch response for report " + report.getId() + ": " + r.getData());
				if (r.getResponseCode() == 200) {
					Log.d(Constants.LOGGING_TAG, "Marking report as synched: " + report.getId());
					TanapaDbHelper.getInstance(null).markReportAsSynchronized(report.getId());
				}
			}
			
		});
	}
	
	private void synchronizeUserLogs() {
		List<UserLog> unsynchedLogs = TanapaDbHelper.getInstance(null).findUnsynchronizedLUserLogs();
		Log.d(Constants.LOGGING_TAG, "Number of unsynched user logs found: " + unsynchedLogs.size());
		for (UserLog userLog : unsynchedLogs) {
			Log.d(Constants.LOGGING_TAG, "Synchronizing user log: " + userLog.getId());
			synchronizeUserLog(userLog);
		}
	}
	
	
	private void synchronizeUserLog(final UserLog userLog) {
		JSONObject userLogJsonObject = userLog.toJSON();
		Log.d(Constants.LOGGING_TAG, "User Log Data: " + userLogJsonObject.toString());
		String url = getString(R.string.base_url) + "/user_log.php";
		WebServiceClientHelper.doPost(url, userLogJsonObject.toString(), new ResponseHandler() {
			@Override
			public void onResponse(Response r) {
				Log.d(Constants.LOGGING_TAG, "User log synch response for user log " + userLog.getId() + ": " + r.getData());
				if (r.getResponseCode() == 200) {
					Log.d(Constants.LOGGING_TAG, "Marking user log as synched: " + userLog.getId());
					TanapaDbHelper.getInstance(null).markUserLogAsSynchronized(userLog.getId());
				}
			}
		});
	}
	
	

	
	/*
	
	try {
		formData.put("report_type_id", reportType.getId());
		formData.put("content", contentEditText.getText().toString());
		formData.put("time", Constants.ISO_8601_DATE_FORMAT.format(new Date()));
		formData.put("user_id", id);
		listener.onSerializedFormDataJSON(formData.toString());
	} catch (JSONException e) {
		Log.e(Constants.LOGGING_TAG, "Error occurred while serializing report form data to JSON.", e);
		listener.onSerializedFormDataJSON(null);
	}
	final String url = getString(R.string.base_url) + "/report.php";
	Location location = null;
	// If location services are enabled, get current location.
	GPSTracker gps = GPSTrackerSingleton.getInstance(this);
	if (gps.canGetLocation()) {
		location = gps.getLocation();
		Log.d(Constants.LOGGING_TAG, "Report Location: " + location.getLatitude() + ", " + location.getLongitude());
	}
	serializeFormDataToJSON(new SerializedFormDataJSONListener(){

		@Override
		public void onSerializedFormDataJSON(String data) {
			WebServiceClientHelper.doPost(url, data, new ResponseHandler() {

				@Override
				public void onResponse(Response r) {
					Log.d(Constants.LOGGING_TAG, r.getData());
					
				}
				
			});
			
		}
		
	});
	
}

private void serializeFormDataToJSON(final SerializedFormDataJSONListener listener) {
	User.getId(this, new UserIdListener() {

		@Override
		public void onUserId(Integer id) {
			JSONObject formData = new JSONObject();
			Spinner reportTypeSpinner = (Spinner) findViewById(R.id.report_reportTypeSpinner);
			ReportType reportType = (ReportType) reportTypeSpinner.getSelectedItem();
			EditText contentEditText = (EditText) findViewById(R.id.report_content);
			try {
				formData.put("report_type_id", reportType.getId());
				formData.put("content", contentEditText.getText().toString());
				formData.put("time", Constants.ISO_8601_DATE_FORMAT.format(new Date()));
				formData.put("user_id", id);
				listener.onSerializedFormDataJSON(formData.toString());
			} catch (JSONException e) {
				Log.e(Constants.LOGGING_TAG, "Error occurred while serializing report form data to JSON.", e);
				listener.onSerializedFormDataJSON(null);
			}
			
		}
		
	});
}

private interface SerializedFormDataJSONListener {
	public void onSerializedFormDataJSON(String data);
}
*/
	
}
