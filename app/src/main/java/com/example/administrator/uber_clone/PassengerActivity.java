package com.example.administrator.uber_clone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;
import java.util.Map;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button button;

    //  private Boolean onCancelled = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        button = findViewById(R.id.passengerButton);
        Button signoutButton = findViewById(R.id.passengerSignOutButton);
        signoutButton.setOnClickListener(this);
        button.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        retainButtonState();
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

                Log.i("TAG", "" + location);

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
    }

    private void checksForAndroidVersion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getCurrentLocation();
        } else {
            checksThatLocationGiven();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();


            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checksThatLocationGiven() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {

        settingUpdateLocationToMap(settingLocationListnerAndGetLocation());
    }

    private Location settingLocationListnerAndGetLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void settingUpdateLocationToMap(Location location) {

        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            //          Log.i("latlong", latLng + "");
        } catch (Exception e) {
            FancyToast.makeText(this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.passengerButton:
                if (button.getText().toString().toLowerCase().equals("book a car")) {
                    ParseObject parseObject = new ParseObject("RequestCar");
                    parseObject.put("username", ParseUser.getCurrentUser().getUsername());
                    Location location = settingLocationListnerAndGetLocation();
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLatitude());
                    parseObject.put("Location", parseGeoPoint);
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                FancyToast.makeText(PassengerActivity.this, "Car Booked", FancyToast.LENGTH_LONG,
                                        FancyToast.SUCCESS, true).show();
                                button.setText("Cancel Car");
                            } else {
                                FancyToast.makeText(PassengerActivity.this, e.getMessage(), FancyToast.LENGTH_LONG,
                                        FancyToast.SUCCESS, true).show();
                            }
                        }
                    });
                } else {
                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("RequestCar");
                    parseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> requests, ParseException e) {
                            if (requests != null && requests.size() > 0 && e == null) {
                                for (ParseObject request : requests) {
                                    request.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                button.setText("book a car");
                                                FancyToast.makeText(PassengerActivity.this, "Order cancelled", FancyToast.LENGTH_LONG
                                                        , FancyToast.SUCCESS, true).show();
                                            } else {
                                                FancyToast.makeText(PassengerActivity.this, e.getMessage(), FancyToast.LENGTH_LONG
                                                        , FancyToast.ERROR, true).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.passengerSignOutButton:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            FancyToast.makeText(PassengerActivity.this,"LogOut Successfully",FancyToast.LENGTH_LONG,
                                    FancyToast.SUCCESS,true).show();
                            Intent intent = new Intent(PassengerActivity.this,SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            FancyToast.makeText(PassengerActivity.this,e.getMessage(),FancyToast.LENGTH_LONG,
                                    FancyToast.SUCCESS,true).show();
                        }
                    }
                });

        }
    }

    private void retainButtonState() {
        try {
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("RequestCar");
            parseQuery.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects != null && objects.size() > 0 && e == null) {
    //                    onCancelled = false;
                        Log.i("TAG", "getting request");
                        button.setText("Cancel Car");
                    } else {
                        return;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
