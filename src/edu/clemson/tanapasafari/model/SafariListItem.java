package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SafariListItem extends Safari {
	
	private String tileMediaType;
	private String tileMediaUrl;
	
	public SafariListItem() {
		super();
	}
	
	public SafariListItem(JSONObject jsonObject) {
		super(jsonObject);
		Log.d("JSONObject", jsonObject.toString());
		try {
			if (jsonObject.has("tile_media_type")) {
				this.setTileMediaType(jsonObject.getString("tile_media_type"));
			}
			if (jsonObject.has("tile_media_url")) {
				this.setTileMediaUrl(jsonObject.getString("tile_media_url"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getTileMediaType() {
		return tileMediaType;
	}
	public void setTileMediaType(String tileMediaType) {
		this.tileMediaType = tileMediaType;
	}
	public String getTileMediaUrl() {
		return tileMediaUrl;
	}
	public void setTileMediaUrl(String tileMediaUrl) {
		this.tileMediaUrl = tileMediaUrl;
	}
	
	

}
