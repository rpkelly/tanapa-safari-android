package edu.clemson.tanapasafari.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.clemson.tanapasafari.constants.Constants;

public class Report {

	private long id;
	private long reportTypeId;
	private String content;
	private Date time;
	private double latitude;
	private double longitude;
	private long userId;
	private Media media;
	private boolean synch;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getReportTypeId() {
		return reportTypeId;
	}
	
	public void setReportTypeId(long reportTypeId) {
		this.reportTypeId = reportTypeId;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public boolean isSynch() {
		return synch;
	}

	public void setSynch(boolean synch) {
		this.synch = synch;
	}
	
	public JSONObject toJSON() {
		
		JSONObject reportJson = new JSONObject();
		
		try {
			reportJson.put("id", this.getId());
			reportJson.put("report_type_id", this.getReportTypeId());
			reportJson.put("content", this.getContent());
			reportJson.put("time", Constants.ISO_8601_DATE_FORMAT.format(this.getTime()));
			reportJson.put("latitude", this.getLatitude());
			reportJson.put("longitude", this.getLongitude());
			reportJson.put("user_id", this.getUserId());
			if (this.media != null) {
				//reportJson.put("media", media.toJSON());
			}
			reportJson.put("synch", this.synch);
		} catch (JSONException e) {
			Log.e(Constants.LOGGING_TAG, "Error occurred while serializing Report object to JSON.", e);
		}
		
		return reportJson;
		
	}
	
}
