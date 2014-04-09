package edu.clemson.tanapasafari;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.clemson.tanapasafari.db.TanapaDbHelper;
import edu.clemson.tanapasafari.model.SafariWayPoint;
import edu.clemson.tanapasafari.service.GPSTracker;
import edu.clemson.tanapasafari.service.GPSTrackerSingleton;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GuideActivity extends Activity {
	double latitude;
	double longitude;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Intent intent = getIntent();
        int safariId = intent.getIntExtra("safariId", -1);
 
        //set your starting location
        GPSTracker gpsTracker = GPSTrackerSingleton.getInstance(this);
        if(!gpsTracker.canGetLocation()){
        	gpsTracker.showSettingsAlert();
        }
        if(gpsTracker.canGetLocation()){
        	latitude = gpsTracker.getLatitude();
        	longitude = gpsTracker.getLongitude();
        }
        
        GoogleMap map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(latitude, longitude), 12));
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        
        
        //query internal database for waypoints
        List<SafariWayPoint> wayPoints = new ArrayList<SafariWayPoint>();
        wayPoints = TanapaDbHelper.getInstance(getBaseContext()).getWayPoints(safariId);
        Log.d("WayPoints", wayPoints.toString());
        
        //Set polyline options
        PolylineOptions line = new PolylineOptions();
        line.width(5);
        line.color(Color.GREEN);
        
        //add the waypoints
        for(SafariWayPoint point:wayPoints){
        	line.add(new LatLng(point.getLatitude(), point.getLongitude()));
        }
        
        //add the line
        map.addPolyline(line);
	}
	
}