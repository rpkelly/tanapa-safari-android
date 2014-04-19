package edu.clemson.tanapasafari.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.clemson.tanapasafari.constants.Constants;

public class UserLog {

	private int id;
	private double latitude;
	private double longitude;
	private int userId;
	private Date time;

	public UserLog() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLatitude(){
		return latitude;
	}
	
	public void setLatitude(double lat){
		latitude = lat;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public void setLongitude(double lng){
		longitude = lng;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public JSONObject toJSON() {
		
		JSONObject userLogJson = new JSONObject();
		
		try {
			userLogJson.put("id", this.getId());
			userLogJson.put("time", Constants.ISO_8601_DATE_FORMAT.format(this.getTime()));
			userLogJson.put("latitude", this.getLatitude());
			userLogJson.put("longitude", this.getLongitude());
			userLogJson.put("user_id", this.getUserId());
		} catch (JSONException e) {
			Log.e(Constants.LOGGING_TAG, "Error occurred while serializing UserLog object to JSON.", e);
		}
		
		return userLogJson;
		
	}
	
	
}
