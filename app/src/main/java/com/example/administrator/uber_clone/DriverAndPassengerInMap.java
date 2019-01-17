package com.example.administrator.uber_clone;

import android.content.Intent;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DriverAndPassengerInMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_and_passenger_in_map);
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
        Intent intent = getIntent();
        double dlatitude = intent.getDoubleExtra("dlatitude",45.78);
        double  dLongitude = intent.getDoubleExtra("dlongitude",-34.78);
        Log.i("latlong",dlatitude+","+dLongitude);
        LatLng dlocation = new LatLng(dlatitude, dLongitude);
//        mMap.addMarker(new MarkerOptions().position(dlocation).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(dlocation));
        double platitude = intent.getDoubleExtra("platitude",76);
        double plongitude = intent.getDoubleExtra("plongitude",-98);
        LatLng plocation = new LatLng(platitude, plongitude);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Marker driverMarker = mMap.addMarker(new MarkerOptions().position(dlocation).title("Driver location"));
        Marker passengerMarker = mMap.addMarker(new MarkerOptions().position(plocation).title("Passenger location"));

        ArrayList<Marker> Markers = new ArrayList<>();
        Markers.add(driverMarker);
        Markers.add(passengerMarker);

        for(Marker marker:Markers)
            builder.include(marker.getPosition());

        LatLngBounds latLngBounds = builder.build();

        CameraUpdate cameraUpdate =  CameraUpdateFactory.newLatLngBounds(latLngBounds,0);
        mMap.animateCamera(cameraUpdate);
    }
}
