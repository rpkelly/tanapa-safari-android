package edu.clemson.tanapasafari.service;

import android.content.Context;

public class GPSTrackerSingleton {
	
	private static GPSTracker instance;
	
	public static GPSTracker getInstance(Context c) {
		if (instance == null) {
			instance = new GPSTracker(c);
		}
		return instance;
	}

}
