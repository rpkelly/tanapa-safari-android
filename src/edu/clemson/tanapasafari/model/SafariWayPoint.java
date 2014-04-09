package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

public class SafariWayPoint {
	//SAFARI_WAYPOINTS (id INTEGER PRIMARY KEY, 
		//sequence INTEGER NOT NULL, 
		//latitude DECIMAL NOT NULL, 
		//longitude DECIMAL NOT NULL, 
		//safari_id INTEGER NOT NULL)",

	private int id;
	private int sequence;
	private double latitude;
	private double longitude;
	private int safari_id;
	
	public SafariWayPoint(){
		super();
	}
	
	public SafariWayPoint(JSONObject jsonObject) {
		try {
			if (jsonObject.has("id")){
				this.setId(jsonObject.getInt("id"));
			}
			if (jsonObject.has("sequence")){
				this.setSequence(jsonObject.getInt("sequence"));
			}
			if (jsonObject.has("latitude")) {
				this.setLatitude(jsonObject.getDouble("latitude"));
			}
			if (jsonObject.has("longitude")){
				this.setLongitude(jsonObject.getDouble("longitude"));
			}
			if (jsonObject.has("safari_id")) {
				this.setSafariId(jsonObject.getInt("safari_id"));
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SafariWayPoint(int id, int sequence, double latitude, double longitude, int safari_id){
		this.id = id;
		this.sequence = sequence;
		this.latitude = latitude;
		this.longitude = longitude;
		this.safari_id = safari_id;
	}

	public int getSafariId(){
		return safari_id;
	}
	
	public void setSafariId(int id) {
		this.safari_id = id;
	}

	public double getLongitude(){
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude(){
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getSequence(){
		return sequence;
	}
	
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getId(){
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
