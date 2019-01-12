package com.example.administrator.uber_clone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("TAG",""+location);

                settingUpdateLocationToMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        checksForAndroidVersion();
        // Add a marker in Sydney and move the camera
        //settingUpdateLocationToMap();
        //   Location location = getCurrentLocation();


    }

    private void checksForAndroidVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                Location passangerLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                settingUpdateLocationToMap(passangerLocation);
            }
        } else {
            checksThatLocationGiven();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                    Location passangerLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    settingUpdateLocationToMap(passangerLocation);
                }

            }
            Log.i("onRequest", "" + requestCode);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checksThatLocationGiven() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
//            getCurrentLocation();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                Location passangerLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                settingUpdateLocationToMap(passangerLocation);
            }

        }
    }
//    private Location getCurrentLocation(){
//
//        Location passangerLocation = null;
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
//            passangerLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            settingUpdateLocationToMap(passangerLocation);
//
//        } catch (SecurityException e) {
//            Log.i("error",e.getMessage());
//        }
//
//        return passangerLocation;
//    }

    private void settingUpdateLocationToMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        Log.i("latlong", latLng + "");
    }
}
