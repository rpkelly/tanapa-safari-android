package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.clemson.tanapasafari.constants.Constants;

public class Media {
	
	private long id;
	private String type;
	private String url;
	
	
	public Media () {
		super();
	}
	
	public Media(JSONObject jsonObject) {
		super();
		if (jsonObject.has("id")) {
			try {
				this.id = jsonObject.getLong("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("type")) {
			try {
				this.type = jsonObject.getString("type");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("url")) {
			try {
				this.url = jsonObject.getString("url");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long l) {
		this.id = l;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

public JSONObject toJSON() {
		
		JSONObject mediaJson = new JSONObject();
		
		try {
			mediaJson.put("id", this.getId());
			mediaJson.put("type", this.getType());
			mediaJson.put("url", this.getUrl());
		} catch (JSONException e) {
			Log.e(Constants.LOGGING_TAG, "Error occurred while serializing Report object to JSON.", e);
		}
		
		return mediaJson;
		
	}
	
}
