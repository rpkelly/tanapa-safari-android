package edu.clemson.tanapasafari.geofence;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {

        // Instance variables
        private String mId;
        private double mLatitude;
        private double mLongitude;
        private float mRadius;
        private long mExpirationDuration;
        private int mTransitionType;
        public static final long NEVER_EXPIRE = -1;

        
    public SimpleGeofence(
            String geofenceId,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        // Set the instance fields from the constructor
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mExpirationDuration = expiration;
        this.mTransitionType = transition;
    }
    // Instance field getters
    public String getId() {
        return mId;
    }   
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public float getRadius() {
        return mRadius;
    }
    public long getExpirationDuration() {
        return mExpirationDuration;
    }
    public int getTransitionType() {
        return mTransitionType;
    }

    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(NEVER_EXPIRE)
                .build();
    }

}
