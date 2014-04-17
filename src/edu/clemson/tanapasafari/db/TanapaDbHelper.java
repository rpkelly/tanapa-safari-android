package edu.clemson.tanapasafari.db;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.SafariPointOfInterest;
import edu.clemson.tanapasafari.model.SafariWayPoint;
import edu.clemson.tanapasafari.model.UserLog;

public class TanapaDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 8;
	public static final String DATABASE_NAME = "tanapa.db";
	
	
	private static final String[] SQL_CREATE_ENTRIES = {
		"CREATE TABLE MEDIA ( id INTEGER PRIMARY KEY, type VARCHAR(20) NOT NULL, url VARCHAR(255) NOT NULL)",
		"CREATE TABLE REPORT_TYPE (id INTEGER PRIMARY KEY, name	VARCHAR(80) NOT NULL)",
		"CREATE TABLE REPORT ( id INTEGER PRIMARY KEY, report_type_id INTEGER NOT NULL, content	TEXT, time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, latitude DECIMAL, longitude DECIMAL, user_id INTEGER NOT NULL, report_media_id INTEGER, synchronized INTEGER DEFAULT(0))",
		"CREATE TABLE SAFARI ( id INTEGER PRIMARY KEY, name	VARCHAR(80) NOT NULL, description TEXT, header_media_id INTEGER, footer_media_id INTEGER, tile_media_id	INTEGER)",
		"CREATE TABLE SAFARI_WAYPOINTS (id INTEGER PRIMARY KEY, sequence INTEGER NOT NULL, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, safari_id INTEGER NOT NULL)",
		"CREATE TABLE SAFARI_POINTS_OF_INTEREST ( id INTEGER PRIMARY KEY, name VARCHAR(80) NOT NULL, safari_id INTEGER NOT NULL, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, radius INTEGER NOT NULL)",
		"CREATE TABLE USER ( id INTEGER PRIMARY KEY, user_id INTEGER)",
		"CREATE TABLE USER_LOG (id INTEGER PRIMARY KEY, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, loaded INTEGER NOT NULL DEFAULT 0)"
	};
	
	private static final String[] SQL_DELETE_ENTRIES = {"DROP TABLE IF EXISTS SAFARI_POINTS_OF_INTEREST",
		"DROP TABLE IF EXISTS SAFARI_WAYPOINTS",
		"DROP TABLE IF EXISTS REPORT",
		"DROP TABLE IF EXISTS REPORT_TYPE",
		"DROP TABLE IF EXISTS USER_LOG",
		"DROP TABLE IF EXISTS USER",
		"DROP TABLE IF EXISTS SAFARI",
		"DROP TABLE IF EXISTS MEDIA",
		"DROP TABLE IF EXISTS USER",
		"DROP TABLE IF EXISTS USER_LOG"
	};
	
	private static TanapaDbHelper instance;
	
	
	public static TanapaDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new TanapaDbHelper(context);
		}
		return instance;
	}
	
	
	private TanapaDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		executeSqlArray(db, SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		executeSqlArray(db, SQL_DELETE_ENTRIES);
		onCreate(db);
	}
	
	private void executeSqlArray(SQLiteDatabase db, String[] sqlArray) {
		for (String q : sqlArray) {
			db.execSQL(q);
		}
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
	
	public long saveReport(Report report) {
		if ( report == null) {
			return -1;
		}
		
		if ( report.getMedia() != null ) {
			ContentValues mediaContentValues = new ContentValues();
			mediaContentValues.put("type", report.getMedia().getType());
			mediaContentValues.put("url", report.getMedia().getUrl());
			report.getMedia().setId(this.getWritableDatabase().insert("MEDIA", "type", mediaContentValues));
		}
		
		ContentValues reportContentValues = new ContentValues();
		reportContentValues.put("report_type_id", report.getReportTypeId());
		reportContentValues.put("content", report.getContent());
		reportContentValues.put("time", Constants.ISO_8601_DATE_FORMAT.format(report.getTime()));
		reportContentValues.put("latitude", report.getLatitude());
		reportContentValues.put("longitude", report.getLongitude());
		reportContentValues.put("user_id", report.getUserId());
		reportContentValues.put("report_media_id", report.getMedia() != null ? report.getMedia().getId() : null);
		report.setId(this.getWritableDatabase().insert("REPORT", "content", reportContentValues));
		
		return report.getId();
		
	}
	
	public List<Report> findUnsynchronizedReports() {
		List<Report> reports = new ArrayList<Report>();
		
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT REPORT.id report_id, report_type_id, content, time, latitude, longitude, user_id, report_media_id, MEDIA.type report_media_type, MEDIA.url report_media_url FROM REPORT LEFT JOIN MEDIA ON MEDIA.id = REPORT.report_media_id WHERE synchronized = 0", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				Report report = new Report();
				report.setId(cursor.getLong(cursor.getColumnIndex("report_id")));
				report.setReportTypeId(cursor.getLong(cursor.getColumnIndex("report_type_id")));
				report.setContent(cursor.getString(cursor.getColumnIndex("content")));
				try {
					report.setTime(Constants.ISO_8601_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex("time"))));
				} catch (ParseException e) {
					Log.e(Constants.LOGGING_TAG, "Failed converting date from database to Date class", e);
				}
				report.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
				report.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
				report.setUserId(cursor.getLong(cursor.getColumnIndex("user_id")));
				reports.add(report);
			}
			cursor.close();
		}
		return reports;
	}
	
	
	public void markReportAsSynchronized(long reportId) {
		ContentValues values = new ContentValues();
		values.put("synchronized", 1);
		this.getWritableDatabase().update("REPORT", values, "id = ?", new String[]{Long.toString(reportId)});
	}
	
	public void clearPOIs(){
		this.getReadableDatabase().delete("SAFARI_POINTS_OF_INTEREST", null, null);
	}
	
	public void clearWayPoints(){
		this.getWritableDatabase().delete("SAFARI_WAYPOINTS", null, null);
	}
	
	public void clearPointsOfInterest() {
		this.getWritableDatabase().delete("SAFARI_POINTS_OF_INTEREST", null, null);
	}
	
	public void saveWayPoint(SafariWayPoint wp){
		ContentValues wayPointContentValues = new ContentValues();
		wayPointContentValues.put("sequence", wp.getSequence());
		wayPointContentValues.put("latitude", wp.getLatitude());
		wayPointContentValues.put("longitude", wp.getLongitude());
		wayPointContentValues.put("safari_id", wp.getSafariId());
		Log.d("inserted waypoint", "Waypoint sequence:" + wp.getSequence());
		this.getWritableDatabase().insert("SAFARI_WAYPOINTS", null, wayPointContentValues);
	}
	
	public List<SafariWayPoint> getWayPoints(int safari_id){
		List<SafariWayPoint> waypoints = new ArrayList<SafariWayPoint>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM SAFARI_WAYPOINTS WHERE safari_id = "+ safari_id +" ORDER BY sequence ASC", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				SafariWayPoint wp = new SafariWayPoint(cursor.getInt(cursor.getColumnIndex("id")), 
						cursor.getInt(cursor.getColumnIndex("sequence")), 
						cursor.getDouble(cursor.getColumnIndex("latitude")), 
						cursor.getDouble(cursor.getColumnIndex("longitude")), 
						cursor.getInt(cursor.getColumnIndex("safari_id")));
				waypoints.add(wp);
			}
			cursor.close();
		}
		return waypoints;
	}
	
	public void saveSafariPointOfInterest(SafariPointOfInterest poi) {
		ContentValues poiContentValues = new ContentValues();
		poiContentValues.put("id", poi.getId());
		poiContentValues.put("name", poi.getName());
		poiContentValues.put("latitude", poi.getLatitude());
		poiContentValues.put("longitude", poi.getLongitude());
		poiContentValues.put("radius", poi.getRadius());
		poiContentValues.put("safari_id", poi.getSafariId());
		this.getWritableDatabase().insert("SAFARI_POINTS_OF_INTEREST", null, poiContentValues);
	}
	
	public List<SafariPointOfInterest> getSafariPointsOfInterest(int safariId) {
		List<SafariPointOfInterest> results = new ArrayList<SafariPointOfInterest>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM SAFARI_POINTS_OF_INTEREST", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				SafariPointOfInterest poi = new SafariPointOfInterest();
				poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
				poi.setName(cursor.getString(cursor.getColumnIndex("name")));
				poi.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
				poi.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
				poi.setRadius(cursor.getInt(cursor.getColumnIndex("radius")));
				poi.setSafariId(cursor.getInt(cursor.getColumnIndex("safari_id")));
				results.add(poi);
			}
			cursor.close();
		}
		return results;
	}

	public boolean hasUser(){
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM USER", null);
		if(cursor != null){
			return true;
		}
		else
			return false;
	}
	
	public void addUser(int uId){
		ContentValues userContentValues = new ContentValues();
		userContentValues.put("user_id", uId);
		this.getWritableDatabase().insert("USER", null, userContentValues);

	}
	
	public int getUser(){
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT user_id FROM user", null);
		int userID = -1;
		if(hasUser()){
			while(cursor.moveToNext()){
				userID = cursor.getInt(cursor.getColumnIndex("user_id"));
			}
		}
		return userID;
	}
	
	public void saveLocation(UserLog log){
		ContentValues locContentValues = new ContentValues();
		locContentValues.put("latitude", log.getLatitude());
		locContentValues.put("longitude", log.getLongitude());
		this.getWritableDatabase().insert("USER_LOG", null, locContentValues);

	}

	public String getUnPostedLogs(){
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT longitude,  latitude, time FROM user_log WHERE loaded = 0", null);
		JSONObject logs = new JSONObject();
		JSONObject log;
		JSONArray arr = new JSONArray();
		int user = getUser();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				log = new JSONObject();
				try {
					log.put("longitude", cursor.getDouble(cursor.getColumnIndex("longitude")));
					log.put("latitude", cursor.getDouble(cursor.getColumnIndex("latitude")));
					log.put("time", cursor.getLong(cursor.getColumnIndex("time")));
					log.put("user_id", user);
					arr.put(log);				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			}
			try{
				logs.put("logs", arr);
			} catch(JSONException e){
				e.printStackTrace();
			}
			cursor.close();
		}
		return logs.toString();
	}
	
	public void markPosted(){
		this.getReadableDatabase().rawQuery("UPDATE user_log SET loaded = 1 WHERE loaded = 0", null);
	}
}
