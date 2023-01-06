package com.visionxoft.abacus.rehmantravel.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.visionxoft.abacus.rehmantravel.R;
import com.visionxoft.abacus.rehmantravel.activity.MainActivity;
import com.visionxoft.abacus.rehmantravel.model.Constants;
import com.visionxoft.abacus.rehmantravel.task.GetCurrentAirportLocation;

/**
 * Helper class for Google API and Location related functions
 */
public class GoogleAPIHelper {

    private FusedLocationProviderApi api;

    /**
     * Initialize FusedLocationProviderApi
     */
    public GoogleAPIHelper() {
        api = LocationServices.FusedLocationApi;
    }

    /**
     * Build and return Google client to access API
     *
     * @param context       Context
     * @param conn_callback ConnectionCallbacks Interface
     * @return Google API Client object
     */
    public GoogleApiClient getGoogleClient(Context context, GoogleApiClient.ConnectionCallbacks conn_callback) {
        return new GoogleApiClient.Builder(context)
                //.addApi(AppIndex.API) //App Index API
                .addApi(LocationServices.API) // Location Service API
                .addConnectionCallbacks(conn_callback) // Client Connectivity Callbacks
                .build();
    }

    /**
     * Get current airport location
     *
     * @param activity     Parent activity class
     * @param client       Google API client object
     * @param showFeedback true if want to display errors or feedback to user, else false
     */
    public void getCurrentAirportLocation(final MainActivity activity, final GoogleApiClient client, final boolean showFeedback) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Location last_loc = api.getLastLocation(client);
                if (last_loc != null) {
                    new GetCurrentAirportLocation(activity, last_loc, showFeedback).execute();
                } else if (showFeedback)
                    PhoneFunctionality.makeToast(activity, activity.getString(R.string.location_not_available));
            } else {
                if (showFeedback)
                    ActivityCompat.requestPermissions(activity, new String[]
                            {Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_ACCESS_COARSE_LOCATION);

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (showFeedback)
                PhoneFunctionality.makeToast(activity, activity.getString(R.string.error));
        }
    }

    /**
     * Setup Location Request object
     *
     * @return LocationRequest object
     */
    private LocationRequest getLocationRequest() {
        int LOCATION_INTERVAL = 5 * 60 * 1000;
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(LOCATION_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_INTERVAL);
        return locationRequest;
    }
}
