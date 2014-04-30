package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

public class SafariPointOfInterest {
	
	private int id;
	private String name;
	private Media media;
	private double latitude;
	private double longitude;
	private int radius;
	private int safariId;
	private boolean inGeofence = false;
	private Location location;
	private boolean displayed = false;
	
	
	
	public SafariPointOfInterest() {
		super();
	}
	
	public SafariPointOfInterest(JSONObject jsonObject) {
		if (jsonObject.has("id")) {
			try {
				this.id = jsonObject.getInt("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("name")) {
			try {
				this.name = jsonObject.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("latitude")) {
			try {
				this.latitude = jsonObject.getDouble("latitude");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("longitude")) {
			try {
				this.longitude = jsonObject.getDouble("longitude");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("radius")) {
			try {
				this.radius = jsonObject.getInt("radius");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (jsonObject.has("safari_id")) {
			try {
				this.safariId = jsonObject.getInt("safari_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if ( jsonObject.has("media")) {
			try {
				this.media = new Media(jsonObject.getJSONObject("media"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getSafariId() {
		return safariId;
	}
	public void setSafariId(int safariId) {
		this.safariId = safariId;
	}	
	
	public boolean isInGeofence() {
		return inGeofence;
	}
	
	public void setInGeofence(boolean b) {
		inGeofence = b;
	}
	
	public Location getLocation() {
		if (location == null) {
			location = new Location("");
			location.setLatitude(latitude);
			location.setLongitude(longitude);
		}
		return location;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	@Override
	public String toString() {
		return "SafariPointOfInterest [id=" + id + ", name=" + name
				+ ", media=" + media + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", radius=" + radius
				+ ", safariId=" + safariId + ", inGeofence=" + inGeofence
				+ ", displayed=" + displayed + "]";
	}
	
	
	
	
	
}
