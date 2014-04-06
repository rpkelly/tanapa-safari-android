package edu.clemson.tanapasafari.db;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.clemson.tanapasafari.constants.Constants;
import edu.clemson.tanapasafari.model.Report;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TanapaDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "tanapa.db";
	
	
	private static final String[] SQL_CREATE_ENTRIES = {"CREATE TABLE USER_LOG (id INTEGER(30) PRIMARY KEY, user_id INTEGER(30) NOT NULL, latitude DECIMAL(4,6) NOT NULL, longitude DECIMAL(4,6) NOT NULL, time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)",
		"CREATE TABLE MEDIA ( id INTEGER(30) PRIMARY KEY, type VARCHAR(20) NOT NULL, url VARCHAR(256) NOT NULL)",
		"CREATE TABLE REPORT_TYPE (id INTEGER(30) PRIMARY KEY, name	VARCHAR(80) NOT NULL)",
		"CREATE TABLE REPORT ( id INTEGER(30) PRIMARY KEY, report_type_id INTEGER(30) NOT NULL, content	TEXT, time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, latitude DECIMAL(4,6), longitude DECIMAL(4,6), user_id	INTEGER(30) NOT NULL, report_media_id INTEGER(30), syncronized INTEGER DEFAULT(0))",
		"CREATE TABLE SAFARI ( id INTEGER(30) PRIMARY KEY, name	VARCHAR(80) NOT NULL, description TEXT, header_media_id INTEGER(30), footer_media_id INTEGER(30), tile_media_id	INTEGER(30))",
		"CREATE TABLE SAFARI_WAYPOINTS (id INTEGER(30) PRIMARY KEY, sequence INTEGER(15) NOT NULL, latitude DECIMAL(4,6) NOT NULL, longitude DECIMAL(4,6) NOT NULL, safari_id INTEGER(30) NOT NULL)",
		"CREATE TABLE SAFARI_POINTS_OF_INTEREST ( id INTEGER(30) PRIMARY KEY, name VARCHAR(80) NOT NULL, safari_id INTEGER(30) NOT NULL, latitude DECIMAL(4,6) NOT NULL, longitude DECIMAL(4,6) NOT NULL, radius INTEGER(11) NOT NULL)"
	};
	
	private static final String[] SQL_DELETE_ENTRIES = {"DROP TABLE IF EXISTS SAFARI_POINTS_OF_INTEREST",
		"DROP TABLE IF EXISTS SAFARI_WAYPOINTS",
		"DROP TABLE IF EXISTS REPORT",
		"DROP TABLE IF EXISTS REPORT_TYPE",
		"DROP TABLE IF EXISTS USER_LOG",
		"DROP TABLE IF EXISTS USER",
		"DROP TABLE IF EXISTS SAFARI",
		"DROP TABLE IF EXISTS MEDIA"
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
	
	/*
	public long saveTranslation(Translation t) {
		ContentValues pigLatinTextValues = new ContentValues();
		pigLatinTextValues.put("text", t.getPigLatinText());
		long pigLatinTextId = this.getWritableDatabase().insert("PIGLATIN_TEXT", "text", pigLatinTextValues);
		
		ContentValues englishTextValues = new ContentValues();
		englishTextValues.put("text", t.getEnglishText());
		long englishTextId = this.getWritableDatabase().insert("ENGLISH_TEXT", "text", englishTextValues);
		
		ContentValues translationHistoryValues = new ContentValues();
		translationHistoryValues.put("english_text_id", englishTextId);
		translationHistoryValues.put("piglatin_text_id", pigLatinTextId);
		long translationHistoryId = this.getWritableDatabase().insert("TRANSLATION_HISTORY", "translation_time", translationHistoryValues);
		
		return translationHistoryId;
	}
	
	
	public List<Translation> getTranslationHistory() {
		List<Translation> translationHistory = new ArrayList<Translation>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT TH.id, PL.text piglatin_text, EN.text english_text "
				+ "FROM TRANSLATION_HISTORY TH "
				+ "JOIN ENGLISH_TEXT EN ON EN.id = TH.english_text_id "
				+ "JOIN PIGLATIN_TEXT PL ON PL.id = TH.piglatin_text_id "
				+ "ORDER BY translation_time DESC;", null);
		if (cursor != null) {
			while(cursor.moveToNext()) {
				Translation t = new Translation();
				t.setEnglishText(cursor.getString(cursor.getColumnIndex("english_text")));
				t.setPigLatinText(cursor.getString(cursor.getColumnIndex("piglatin_text")));
				translationHistory.add(t);
			}
			cursor.close();
		}
		return translationHistory;
	}
	
	public long savePiglatinEnglishMapping(String piglatin, String english) {
		if (getEnglishWordForPigLatin(piglatin) != null) {
			return -1;
		}
		ContentValues mapping = new ContentValues();
		mapping.put("piglatin_word", piglatin);
		mapping.put("english_word", english);
		long id = -1;
		id = this.getWritableDatabase().insert("PIGLATIN_ENGLISH_MAPPING", "english_word", mapping);
		return id;
	}
	
	
	public Map<String, String> getPiglatinEnglishMappings() {
		Map<String, String> mappings = new HashMap<String, String>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT piglatin_word, english_word FROM PIGLATIN_ENGLISH_MAPPING", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				mappings.put(cursor.getString(cursor.getColumnIndex("piglatin_word")), cursor.getString(cursor.getColumnIndex("english_word")));
			}
			cursor.close();
		}
		return mappings;
	}
	
	public int deletePiglatinEnglishMapping(String piglatin) {
		return this.getWritableDatabase().delete("PIGLATIN_ENGLISH_MAPPING", "piglatin_word = ?", new String[]{piglatin});
	}
	
	public int updatePiglatinEnglishMapping(String piglatin, String english) {
		ContentValues values = new ContentValues();
		values.put("piglatin_word", piglatin);
		values.put("english_word", english);
		return this.getWritableDatabase().update("PIGLATIN_ENGLISH_MAPPING", values, "piglatin_word = ?", new String[]{piglatin});
	}
	
	public String getEnglishWordForPigLatin(String piglatin) {
		String englishWord = null;
		String[] args = {piglatin};
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT english_word FROM PIGLATIN_ENGLISH_MAPPING WHERE piglatin_word = ?", args);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				englishWord = cursor.getString(cursor.getColumnIndex("english_word"));
			}
			cursor.close();
		}
		return englishWord;
	}
	
	public List<Translation> getUnsynchronizedTranslations() {
		List<Translation> translations = new ArrayList<Translation>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT PL.text piglatin_text, EN.text english_text, translation_time "
				+ "FROM TRANSLATION_HISTORY TH "
				+ "JOIN ENGLISH_TEXT EN ON EN.id = TH.english_text_id "
				+ "JOIN PIGLATIN_TEXT PL ON PL.id = TH.piglatin_text_id "
				+ "WHERE TH.synchronized = 0 "
				+ "ORDER BY translation_time DESC;", null);
		
		if (cursor != null) {
			while(cursor.moveToNext()) {
				Translation t = new Translation();
				t.setEnglishText(cursor.getString(cursor.getColumnIndex("english_text")));
				t.setPigLatinText(cursor.getString(cursor.getColumnIndex("piglatin_text")));
				t.setTranslationTime(cursor.getString(cursor.getColumnIndex("translation_time")));
				translations.add(t);
			}
			cursor.close();
		}
		return translations;
	}
	
	
	public Map<String,String> getUnsynchronizedPigLatinToEnglishMappings() {
		Map<String, String> mappings = new HashMap<String, String>();
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT piglatin_word, english_word FROM PIGLATIN_ENGLISH_MAPPING WHERE synchronized = 0", null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				mappings.put(cursor.getString(cursor.getColumnIndex("piglatin_word")), cursor.getString(cursor.getColumnIndex("english_word")));
			}
			cursor.close();
		}
		return mappings;	
	}
	
	
	public void markAllRecordsSynchronized() {
		ContentValues values = new ContentValues();
		values.put("synchronized", 1);
		this.getWritableDatabase().update("PIGLATIN_ENGLISH_MAPPING", values, null, null);
		this.getWritableDatabase().update("TRANSLATION_HISTORY", values, null, null);
	}
	
	
	public void resetDatabase() {
		SQLiteDatabase db = this.getWritableDatabase();
		executeSqlArray(db, SQL_DELETE_ENTRIES);
		onCreate(db);
	}
	
	*/
	
}
