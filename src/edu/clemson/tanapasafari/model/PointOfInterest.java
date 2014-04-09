package edu.clemson.tanapasafari.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PointOfInterest {
	
	private int id;
	private String name;
	private int safariId;
	private double latitude;
	private double longitude;
	private double radius;
	private int headerMediaId;
	
	public PointOfInterest(){
		super();
	}
	
	public PointOfInterest(JSONObject jsonObject){
		try{
			if (jsonObject.has("id")){
				this.setId(jsonObject.getInt("id"));
			}
			if (jsonObject.has("name")){
				this.setName(jsonObject.getString("name"));
			}
			if (jsonObject.has("safari_id")){
				this.setSafariId(jsonObject.getInt("safari_id"));
			}
			if (jsonObject.has("latitude")){
				this.setLatitude(jsonObject.getDouble("latitude"));
			}
			if (jsonObject.has("longitude")){
				this.setLongitude(jsonObject.getDouble("longitude"));
			}
			if (jsonObject.has("radius")){
				this.setRadius(jsonObject.getDouble("radius"));
			}
			if (jsonObject.has("header_media_id")){
				this.setHeaderMediaId(jsonObject.getInt("header_media_id"));
			}
		} catch(JSONException e){
			e.printStackTrace();
		}		
	}
	
	public PointOfInterest(int id, String name, int sid, double lat, double lon, double rad){
		this.id = id;
		this.name = name;
		this.safariId = sid;
		this.latitude = lat;
		this.longitude = lon;
		this.radius = rad;
	}
	
	public int getId(){
		return this.id;
	}
    public void setId(int i){
    	this.id = i;
    }   
    public String getName(){
    	return this.name;
    }
    public void setName(String n){
    	this.name = n;
    }
    public int getSafariId(){
    	return this.safariId;
    }
    public void setSafariId(int s){
    	this.safariId = s;
    }
    public double getLatitude(){
    	return this.latitude;
    }
    public void setLatitude(double l){
    	this.latitude = l;
    }
    public double getLongitude(){
    	return this.longitude;
    }
    public void setLongitude(double l){
    	this.longitude = l;
    }
    public double getRadius(){
    	return this.radius;
    }
    public void setRadius(double r){
    	this.radius = r;
    }
    public int getHeaderMediaId(){
    	return this.headerMediaId;
    }
    public void setHeaderMediaId(int h){
    	this.headerMediaId = h;
    }
    
}
