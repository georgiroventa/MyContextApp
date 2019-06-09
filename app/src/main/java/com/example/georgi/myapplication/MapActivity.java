package com.example.georgi.myapplication;

import android.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap, mMap1, mMap2;
    private List<Marker> originMarkers = new ArrayList<>();
    private GoogleApiClient mClient;
    private Circle circle1, circle2;
    private String window1;
    private String window2;
    private String location1;
    private String location2;
    private String activity1;
    private String activity2;
    private String temperature1 = "" + KmeansActivity.clusters[0][2];
    private String temperature2 = "" + KmeansActivity.clusters[1][2];
    private String humidity1 = "" + KmeansActivity.clusters[0][3];
    private String humidity2 = "" + KmeansActivity.clusters[1][3];
    private String lat_lng1 = "Latitude: " + KmeansActivity.clusters[0][0] + " and " + "longitude: " + KmeansActivity.clusters[0][1];
    private String lat_lng2 = "Latitude: " + KmeansActivity.clusters[1][0] + " and " + "longitude: " + KmeansActivity.clusters[1][1];
    private Object obj = new Object();
    //get current location
    private LocationManager locationManager;
    private LocationListener listener;
    double latitude;
    double longitude;

    private final int REQ_PERMISSION = 5;

    HashMap<String, MarkerHolder> markerHolderMap1 = new HashMap<String, MarkerHolder>();
    HashMap<String, MarkerHolder> markerHolderMap2 = new HashMap<String, MarkerHolder>();
    Marker marker1;
    Marker marker2;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        for(int i = 0; i < 2; i++){
            for(int j = 0; j < KmeansActivity.clusters[i].length; j++) {
                System.out.println(KmeansActivity.clusters[i][j]);
            }
            System.out.println();
        }

        location1 = getAddress(KmeansActivity.clusters[0][0], KmeansActivity.clusters[0][1]);
        location2 = getAddress(KmeansActivity.clusters[1][0], KmeansActivity.clusters[1][1]);
        activity1 = determineActivity(KmeansActivity.clusters[0][4]);
        activity2 = determineActivity(KmeansActivity.clusters[1][4]);
        window1 = "Type of activity: " + activity1 + "\nTemperature: " + (int)KmeansActivity.clusters[0][2] + "(°C)" + " and " + (int)KmeansActivity.clusters[0][3] + "% humidity";
        window2 = "Type of activity: " + activity2 + "\nTemperature: " + (int)KmeansActivity.clusters[1][2] + "(°C)" + " and " + (int)KmeansActivity.clusters[1][3] + "% humidity";
        /*start bottom menu*/
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_home:
                        Intent intent0 = new Intent(MapActivity.this, MainActivity.class);
                        startActivity(intent0);
                        break;
                    case R.id.nav_map:
                        Intent intent1 = new Intent(MapActivity.this, MapActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_noise:
                        Intent intent2 = new Intent(MapActivity.this, NoiseLevelActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_coordinates:
                        Intent intent3 = new Intent(MapActivity.this, RequestActivity.class);
                        startActivity(intent3);
                        break;



                    case R.id.nav_notification:
                        Intent intent4 = new Intent(MapActivity.this, KmeansActivity.class);
                        startActivity(intent4);
                        break;

                }

                return false;
            }
        });

        /* end bottom menu */


        //awareness some try
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mClient.connect();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);
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
        mMap1 = googleMap;
        mMap2 = googleMap;
        LatLng hcmus = new LatLng(/*45.7497836*/ latitude, /*21.2428627 */ longitude);
        LatLng hcmus1 = new LatLng(KmeansActivity.clusters[0][0],KmeansActivity.clusters[0][1]);
        LatLng hcmus2 = new LatLng(KmeansActivity.clusters[1][0],KmeansActivity.clusters[1][1]);
        circle1 = mMap1.addCircle(new CircleOptions()
                    .center(hcmus1)
                    .radius(100)
                    .strokeWidth(5)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.rgb(153, 195, 216))
                    .clickable(true));
       // circle1.setTag(new DataAboutContextUser("merege", 90, 45.56f, 21.57f, 27, 3456));
        circle2 = mMap2.addCircle(new CircleOptions()
                    .center(hcmus2)
                    .radius(100)
                    .strokeWidth(5)
                    .strokeColor(Color.GREEN)
                    .fillColor(Color.rgb(226, 181, 181))
                    .clickable(true));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        mMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus1, 18));
        mMap2.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus2, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .position(hcmus)));
        marker1 = mMap1.addMarker(new MarkerOptions()
                .position(hcmus1)
                .title(location1)
                .snippet(window1));
        marker2 = mMap2.addMarker(new MarkerOptions()
                .position(hcmus2)
                .title(location2)
                .snippet(window2));
        MarkerHolder mholder1 = new MarkerHolder(activity1, temperature1, humidity1, lat_lng1 );
        markerHolderMap1.put(marker1.getId(), mholder1);

        mMap1.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                View v = getLayoutInflater().inflate(R.layout.customlayout1, null);

                TextView tLocation = (TextView) v.findViewById(R.id.paq1);

                TextView tSnippet = (TextView) v.findViewById(R.id.names1);

                TextView tDates = (TextView) v.findViewById(R.id.dates1);

                TextView tPlaces = (TextView) v.findViewById(R.id.places1);

                //These are standard, just uses the Title and Snippet
                tLocation.setText(arg0.getTitle());

                tSnippet.setText(arg0.getSnippet());

                //Now get the extra info you need from the HashMap
                //Store it in a MarkerHolder Object
                MarkerHolder mHolder = markerHolderMap1.get(arg0.getId()); //use the ID to get the info

                tDates.setText("" + mHolder.latitude_longitude_marker );

                //tPlaces.setText(mHolder.latitude_longitude_marker + " " + mHolder.activity_marker);

                return v;
            }
        });

        mMap1.setOnInfoWindowClickListener(this);


        MarkerHolder mholder2 = new MarkerHolder(activity2, temperature2, humidity2, lat_lng2 );
        markerHolderMap2.put(marker2.getId(), mholder2);

        mMap2.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                View v = getLayoutInflater().inflate(R.layout.customlayout2, null);

                TextView tLocation = (TextView) v.findViewById(R.id.paq2);

                TextView tSnippet = (TextView) v.findViewById(R.id.names2);

                TextView tDates = (TextView) v.findViewById(R.id.dates2);

                TextView tPlaces = (TextView) v.findViewById(R.id.places2);

                //These are standard, just uses the Title and Snippet
                tLocation.setText(arg0.getTitle());

                tSnippet.setText(arg0.getSnippet());

                //Now get the extra info you need from the HashMap
                //Store it in a MarkerHolder Object
                MarkerHolder mHolder = markerHolderMap2.get(arg0.getId()); //use the ID to get the info

                tDates.setText(mHolder.latitude_longitude_marker);

                //tPlaces.setText(mHolder.latitude_longitude_marker + " " + mHolder.activity_marker);

                return v;
            }
        });



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap1.setMyLocationEnabled(true);
        mMap2.setMyLocationEnabled(true);
    }
    //create geofence
    @NonNull
    private Geofence getGeofence() {

        return new Geofence.Builder()
                .setRequestId("geofence cluster 1")
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(KmeansActivity.clusters[0][0],KmeansActivity.clusters[0][1], 200)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences((List<Geofence>) getGeofence());
        return builder.build();
    }


    @Override
    public void onBackPressed() {
        Intent intent=new Intent(MapActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("Some", "OnConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private String getAddress(float latitude, float longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String address="";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            String  add = obj.getAddressLine(0);

            Log.e("Location", "Address" + add);
            address=add;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    /*determine the type of user's activity */
    private String determineActivity(float a){
        String type_activity = "";
        if( a > 0f && a < 1.5f){
            type_activity = "still";
        }
        if( a > 1.5f && a <= 2.5f){
            type_activity = "unknown";
        }
        if( a > 2.5f && a <= 3.5f){
            type_activity = "in vehicle";
        }
        if( a > 3.5f && a <= 4.5f){
            type_activity = "on bicycle";
        }
        if( a > 4.5f && a <= 5.5f){
            type_activity = "on foot";
        }
        if( a > 5.5f && a <= 6.5f){
            type_activity = "running";
        }
        if( a > 6.5f && a <= 7.5f){
            type_activity = "walking";
        }

        return type_activity;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
}

