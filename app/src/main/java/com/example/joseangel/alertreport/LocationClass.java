package com.example.joseangel.alertreport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LocationClass {
    private Location userLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Geocoder geocoder;
    private Context context;
    private LocationStringListener listener;

    public LocationClass(Context context, LocationStringListener listenerLocation) {
        this.listener = listenerLocation;
        this.context = context;
        geocoder = new Geocoder(context, Locale.getDefault());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = new LocationRequest();
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    userLocation = locationResult.getLocations().get(0);
                    stopLocationUpdates();
                    setAddressNameFromLocation();
                }
            }
        };
        startLocationUpdates();
    }

    private void setAddressNameFromLocation() {
        try {
            List<Address> addresses = new ArrayList<>(geocoder.getFromLocation(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    1));
            listener.addressString(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public interface LocationStringListener {
        void addressString(String address);
    }
}
