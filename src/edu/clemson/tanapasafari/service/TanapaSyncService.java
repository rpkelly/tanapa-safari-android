package edu.clemson.tanapasafari.service;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import edu.clemson.tanapasafari.R;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.UserLog;
import edu.clemson.tanapasafari.webservice.Response;
import edu.clemson.tanapasafari.webservice.ResponseHandler;
import edu.clemson.tanapasafari.webservice.WebServiceClientHelper;

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
		
		
		// First upload the report media if it exists.
		if ( report.getMedia() != null ) {
			String mediaUrl = getString(R.string.base_url) + "/media.php";
			File file = new File(report.getMedia().getUrl());
			WebServiceClientHelper.doPost(mediaUrl, file, report.getMedia().getType(), new ResponseHandler() {

				@Override
				public void onResponse(Response r) {
					Log.d(Constants.LOGGING_TAG, "Media synch response: " + r.getData());
					if (r.getResponseCode() == 200) {
						try {
							JSONObject jsonObject = new JSONObject(r.getData());
							report.getMedia().setId(jsonObject.getLong("id"));
							report.getMedia().setType(jsonObject.getString("type"));
							report.getMedia().setUrl(jsonObject.getString("url"));
							String reportUrl = getString(R.string.base_url) + "/report.php";
							JSONObject reportJsonObject = report.toJSON();
							Log.d(Constants.LOGGING_TAG, "Report Data: " + reportJsonObject.toString());
							WebServiceClientHelper.doPost(reportUrl, reportJsonObject.toString(), "application/json", new ResponseHandler() {
	
								@Override
								public void onResponse(Response r) {
									Log.d(Constants.LOGGING_TAG, "Report synch response for report " + report.getId() + ": " + r.getData());
									if (r.getResponseCode() == 200) {
										Log.d(Constants.LOGGING_TAG, "Marking report as synched: " + report.getId());
										TanapaDbHelper.getInstance(null).markReportAsSynchronized(report.getId());
									}
								}
								
							});
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				
			});
		} else {
		
			String reportUrl = getString(R.string.base_url) + "/report.php";
			JSONObject reportJsonObject = report.toJSON();
			Log.d(Constants.LOGGING_TAG, "Report Data: " + reportJsonObject.toString());
			WebServiceClientHelper.doPost(reportUrl, reportJsonObject.toString(), "application/json", new ResponseHandler() {

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
		WebServiceClientHelper.doPost(url, userLogJsonObject.toString(), "application/json", new ResponseHandler() {
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
	
}
