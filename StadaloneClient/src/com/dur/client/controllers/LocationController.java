package com.dur.client.controllers;
 
import javafxports.android.FXActivity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dur.client.model.Cords;
import com.dur.client.model.SimpleLocation;
import com.dur.client.view.PrimaryView;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
 
public class LocationController implements LocationListener {
 
    private final Context mContext;
    private static final Log log = LogFactory.getLog(LocationController.class);
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location; // location
    private SimpleLocation simpleLocation;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000;
    // Declaring a Location Manager
    private LocationManager locationManager;
 
    public LocationController(FXActivity context) {
        this.mContext = context;
    }
 
    public void startGps() {
        try {
        	log.info("##### Starting GPS");
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                log.error("##### No location provider");
            } 
            else {
                // First get location from Network Provider
            	log.info("##### Has location provider");
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    log.info("##### Network");
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                	log.info("##### GPS");
                	//locationManager.removeUpdates(LocationController.this);
                	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }
 
        } 
        catch (Exception e) {
        	StringBuilder logeString = new StringBuilder();
        	for(StackTraceElement eleme : e.getStackTrace()){
        		logeString.append(eleme.getFileName() + " " + eleme.getClassName() + " " + eleme.getMethodName() + " " + eleme.getLineNumber() + "/n");
        	}
            log.error("##### Error during starting gps " + e.getMessage() + "/n" + logeString.toString());
        }
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationController.this);
        }       
    }
     
    @Override
    public void onLocationChanged(Location loc) {
    	log.info("##### Location changed");
        AndroidContextController.getInstance().showAlert("Location changed : Lat: " +  loc.getLatitude()+ " Lng: " + loc.getLongitude());
        this.location = loc;
        location = loc;
        Cords cords = new Cords(loc.getLatitude() , loc.getLongitude());
       	simpleLocation = new SimpleLocation(cords, "New Cords");
        ((PrimaryWindowController) MainViewController.getControllerForView(PrimaryView.class)).setCurrentLocation(simpleLocation);
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    	log.info("##### Provider disabled");
//    	stopUsingGPS();
//    	startGps();
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    	log.info("##### Provider enabled");
//    	stopUsingGPS();
//    	startGps();
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	log.info("##### Status changed to " + status + " for provider " + provider);
//    	stopUsingGPS();
//    	startGps();
    }
 
}
