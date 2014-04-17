package edu.clemson.tanapasafari.model;

public class UserLog {
/*	
  "CREATE TABLE USER_LOG (id INTEGER PRIMARY KEY, "
	+ "latitude DECIMAL NOT NULL, "
	+ "longitude DECIMAL NOT NULL, "
	+ "time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
	+ "loaded INTEGER NOT NULL DEFAULT 0)"
*/
	private double latitude;
	private double longitude;

	public UserLog() {
		super();
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
}
