package edu.clemson.tanapasafari.db;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.model.Media;
import edu.clemson.tanapasafari.model.Report;
import edu.clemson.tanapasafari.model.ReportType;
import edu.clemson.tanapasafari.model.SafariPointOfInterest;
import edu.clemson.tanapasafari.model.SafariWayPoint;
import edu.clemson.tanapasafari.model.UserLog;

public class TanapaDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 15;
	public static final String DATABASE_NAME = "tanapa.db";
	
	
	private static final String[] SQL_CREATE_ENTRIES = {
		"CREATE TABLE MEDIA ( id INTEGER PRIMARY KEY, type VARCHAR(20), url VARCHAR(255) NOT NULL)",
		"CREATE TABLE REPORT_TYPE (id INTEGER PRIMARY KEY, name	VARCHAR(80) NOT NULL)",
		"CREATE TABLE REPORT ( id INTEGER PRIMARY KEY, report_type_id INTEGER NOT NULL, content	TEXT, time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, latitude DECIMAL, longitude DECIMAL, user_id INTEGER NOT NULL, report_media_id INTEGER, synchronized INTEGER DEFAULT(0))",
		"CREATE TABLE SAFARI ( id INTEGER PRIMARY KEY, name	VARCHAR(80) NOT NULL, description TEXT, header_media_id INTEGER, footer_media_id INTEGER, tile_media_id	INTEGER)",
		"CREATE TABLE SAFARI_WAYPOINTS (id INTEGER PRIMARY KEY, sequence INTEGER NOT NULL, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, safari_id INTEGER NOT NULL)",
		"CREATE TABLE SAFARI_POINTS_OF_INTEREST ( id INTEGER PRIMARY KEY, name VARCHAR(80) NOT NULL, media_id INTEGER NOT NULL, safari_id INTEGER NOT NULL, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, radius INTEGER NOT NULL)",
		"CREATE TABLE USER_LOG (id INTEGER PRIMARY KEY, latitude DECIMAL NOT NULL, longitude DECIMAL NOT NULL, time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, user_id INTEGER NOT NULL, synchronized INTEGER NOT NULL DEFAULT 0)"
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
				if (!cursor.isNull(cursor.getColumnIndex("report_media_id"))) {
					Media media = new Media();
					media.setId(cursor.getLong(cursor.getColumnIndex("report_media_id")));
					media.setType(cursor.getString(cursor.getColumnIndex("report_media_type")));
					media.setUrl(cursor.getString(cursor.getColumnIndex("report_media_url")));
					report.setMedia(media);
				}
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
		
		if ( poi.getMedia() != null ) {
			ContentValues mediaContentValues = new ContentValues();
			mediaContentValues.put("type", poi.getMedia().getType());
			mediaContentValues.put("url", poi.getMedia().getUrl());
			poi.getMedia().setId(this.getWritableDatabase().insert("MEDIA", "type", mediaContentValues));
		}
		
		ContentValues poiContentValues = new ContentValues();
		poiContentValues.put("id", poi.getId());
		poiContentValues.put("name", poi.getName());
		poiContentValues.put("latitude", poi.getLatitude());
		poiContentValues.put("longitude", poi.getLongitude());
		poiContentValues.put("radius", poi.getRadius());
		poiContentValues.put("safari_id", poi.getSafariId());
		poiContentValues.put("media_id", poi.getMedia().getId());
		this.getWritableDatabase().insert("SAFARI_POINTS_OF_INTEREST", null, poiContentValues);
	}
	
	public List<SafariPointOfInterest> getSafariPointsOfInterest(int safariId) {
		List<SafariPointOfInterest> results = new ArrayList<SafariPointOfInterest>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT poi.id, poi.name, poi.latitude, poi.longitude, poi.radius, poi.safari_id, poi.media_id, m.type, m.url FROM SAFARI_POINTS_OF_INTEREST poi LEFT JOIN MEDIA m ON m.id = poi.media_id", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				SafariPointOfInterest poi = new SafariPointOfInterest();
				poi.setId(cursor.getInt(cursor.getColumnIndex("id")));
				poi.setName(cursor.getString(cursor.getColumnIndex("name")));
				poi.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
				poi.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
				poi.setRadius(cursor.getInt(cursor.getColumnIndex("radius")));
				poi.setSafariId(cursor.getInt(cursor.getColumnIndex("safari_id")));
				if (!cursor.isNull(cursor.getColumnIndex("media_id"))) {
					Media media = new Media();
					media.setId(cursor.getLong(cursor.getColumnIndex("media_id")));
					media.setType(cursor.getString(cursor.getColumnIndex("type")));
					media.setUrl(cursor.getString(cursor.getColumnIndex("url")));
					poi.setMedia(media);
				}
				results.add(poi);
			}
			cursor.close();
		}
		return results;
	}

	public void saveLocation(UserLog log){
		ContentValues locContentValues = new ContentValues();
		locContentValues.put("time", Constants.ISO_8601_DATE_FORMAT.format(log.getTime()));
		locContentValues.put("latitude", log.getLatitude());
		locContentValues.put("longitude", log.getLongitude());
		locContentValues.put("user_id", log.getUserId());
		this.getWritableDatabase().insert("USER_LOG", null, locContentValues);

	}

	
	public List<UserLog> findUnsynchronizedLUserLogs(){
		List<UserLog> userLogs = new ArrayList<UserLog>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT id, longitude, latitude, time, user_id, synchronized FROM user_log WHERE synchronized = 0", null);
		if ( cursor != null ) {
			while (cursor.moveToNext()) {
				UserLog userLog = new UserLog();
				userLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
				userLog.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
				userLog.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
				try {
					userLog.setTime(Constants.ISO_8601_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex("time"))));
				} catch (ParseException e) {
					Log.e(Constants.LOGGING_TAG, "Failed converting date from database to Date class", e);
				}
				userLog.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
				userLogs.add(userLog);
			}
			cursor.close();
		}
		return userLogs;
	}
	
	
	public void markUserLogAsSynchronized(long userLogId){
		ContentValues values = new ContentValues();
		values.put("synchronized", 1);
		this.getWritableDatabase().update("USER_LOG", values, "id = ?", new String[]{Long.toString(userLogId)});
	}
	
	
	public void saveReportType(ReportType reportType) {
		ContentValues reportTypeValues = new ContentValues();
		reportTypeValues.put("id", reportType.getId());
		reportTypeValues.put("name", reportType.getName());
		this.getWritableDatabase().insert("REPORT_TYPE", null, reportTypeValues);
	}
	
	public List<ReportType> getReportTypes(){
		List<ReportType> reportTypes = new ArrayList<ReportType>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT id, name FROM REPORT_TYPE", null);
		if ( cursor != null ) {
			while (cursor.moveToNext()) {
				ReportType reportType = new ReportType();
				reportType.setId(cursor.getInt(cursor.getColumnIndex("id")));
				reportType.setName(cursor.getString(cursor.getColumnIndex("name")));
				reportTypes.add(reportType);
			}
			cursor.close();
		}
		return reportTypes;
	}
	
	public void deleteReportTypes() {
		this.getWritableDatabase().delete("REPORT_TYPE", null, null);
	}
}
