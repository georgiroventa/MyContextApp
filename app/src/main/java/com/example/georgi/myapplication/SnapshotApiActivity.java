package com.example.georgi.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SnapshotApiActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks {
    private static final int GET_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int GET_WEATHER_PERMISSION_REQUEST_CODE = 3;

    private GoogleApiClient mGoogleApiClient;

    private Button b1, b2, b3, b4, b5;
    private TextView tl;

    private SensorManager sensorManager;
    private Sensor light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_api);

        b1 = (Button)findViewById(R.id.button_activity);
        b2 = (Button)findViewById(R.id.button_headphone);
        b3 = (Button)findViewById(R.id.button_location);
        b4 = (Button)findViewById(R.id.button_weather);
        b5 = (Button)findViewById(R.id.button_light);

        tl = (TextView)findViewById(R.id.textView_light);

        b5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
                light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                tl.setText(""+light);
            }
        });


        buildApiClient();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoLoc();

        }
    }

    private void buildAlertMessageNoLoc() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your location seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*
      Build the google api client to use awareness apis.
    */
    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(SnapshotApiActivity.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        //Google API client connected. Ready to use awareness api

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               getCurrentActivity();

            }
        });
        b2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               getHeadphoneStatus();

            }
        });
        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getLocation();

            }
        });

        b4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getWeather();

            }
        });

       // callSnapShotGroupApis();
    }

    /*
      This method will call all the snapshot group apis.
    */
    private void callSnapShotGroupApis() {
        //get info about user's current activity
        getCurrentActivity();

        //get the current state of the headphones.
        getHeadphoneStatus();

        //get current location. This will need location permission, so first check that.
       /* if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_LOCATION_PERMISSION_REQUEST_CODE);
        } else {*/
            getLocation();
       // }

        //get current place. This will need location permission, so first check that.
       /* if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_PLACE_PERMISSION_REQUEST_CODE);
        } else {*/
            //getPlace();
       // }

        //get current weather conditions. This will need location permission, so first check that.
        /*if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    SnapshotApiActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_WEATHER_PERMISSION_REQUEST_CODE);
        } else {*/
            getWeather();
        //}
}

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case GET_LOCATION_PERMISSION_REQUEST_CODE://location permission granted
                    getLocation();
                    break;

                case GET_WEATHER_PERMISSION_REQUEST_CODE://location permission granted
                    getWeather();
                    break;
            }
       // }
       // else {
           // Toast.makeText(SnapshotApiActivity.this, "Permission was not granted.", Toast.LENGTH_LONG).show();
        //}

    }

    /**
     * Get the current weather condition at current location.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getWeather() {
        if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , GET_WEATHER_PERMISSION_REQUEST_CODE);
            }
            return;
        }
        //noinspection MissingPermission
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<WeatherResult>() {
                        @Override
                        public void onResult(@NonNull WeatherResult weatherResult) {
                            if (!weatherResult.getStatus().isSuccess()) {
                                //Toast.makeText(SnapshotApiActivity.this, "Could not get weather.", Toast.LENGTH_LONG).show();
                               statusCheck();
                                Toast.makeText(SnapshotApiActivity.this, "ERoare vreme.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //parse and display current weather status
                            //to get a Weather instance with the current local weather conditions and make a report
                            Weather weather = weatherResult.getWeather();
                            String weatherReport = "Temperature: " + weather.getTemperature(Weather.CELSIUS)
                                    + "\nHumidity: " + weather.getHumidity();
                            ((TextView) findViewById(R.id.weather_status)).setText(weatherReport);
                        }
                    });
    }


    /**
     * Get user's current location. We are also displaying Google Static map.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(SnapshotApiActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        , GET_LOCATION_PERMISSION_REQUEST_CODE);
            }
            return;
        }
        //noinspection MissingPermission
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get location.", Toast.LENGTH_LONG).show();
                           // statusCheck();
                            //Toast.makeText(SnapshotApiActivity.this, "Eroare.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //get location
                        Location location = locationResult.getLocation();
                        ((TextView) findViewById(R.id.current_latlng)).setText("Latitudine: "+ location.getLatitude() + ",Longitudine: " + location.getLongitude());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.latlng_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("Time & Date: " + sdf.format(new Date(location.getTime())));

                       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);

                        //Load the current map image from Google map
                        String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                                + location.getLatitude() + "," + location.getLongitude()
                                + "&zoom=20&size=400x250&key=" + getString(R.string.google_maps_key);  // key_api = google_maps_key
                        Picasso.with(SnapshotApiActivity.this).load(url).into((ImageView) findViewById(R.id.current_map));
                    }
                });
    }

    /**
     * Check whether the headphones are plugged in or not? This is under snapshot api category.
     */
    private void getHeadphoneStatus() {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get headphone state.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();

                        //display the status
                        TextView headphoneStatusTv = (TextView) findViewById(R.id.headphone_status);
                        headphoneStatusTv.setText(headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in." : "Unplugged.");
                    }
                });
    }

    /**
     * Get current activity of the user. This is under snapshot api category.
     * Current activity and confidence level will be displayed on the screen.
     */
    private void getCurrentActivity() {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get the current activity.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();

                        //set the activity name
                        TextView activityName = (TextView) findViewById(R.id.probable_activity_name);
                        switch (probableActivity.getType()) {
                            case DetectedActivity.IN_VEHICLE:
                                activityName.setText("In vehicle");
                                break;
                            case DetectedActivity.ON_BICYCLE:
                                activityName.setText("On bicycle");
                                break;
                            case DetectedActivity.ON_FOOT:
                                activityName.setText("On foot");
                                break;
                            case DetectedActivity.RUNNING:
                                activityName.setText("Running");
                                break;
                            case DetectedActivity.STILL:
                                activityName.setText("Still");
                                break;
                            case DetectedActivity.TILTING:
                                activityName.setText("Tilting");
                                break;
                            case DetectedActivity.UNKNOWN:
                                activityName.setText("Unknown");
                                break;
                            case DetectedActivity.WALKING:
                                activityName.setText("Walking");
                                break;
                        }

                        //set the confidante level
                        ProgressBar confidenceLevel = (ProgressBar) findViewById(R.id.probable_activity_confidence);
                        confidenceLevel.setProgress(probableActivity.getConfidence());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("Time & Date: " + sdf.format(new Date(ar.getTime())));
                    }
                });
    }

    @Override
    public void onConnectionSuspended(final int i) {
        new AlertDialog.Builder(this)
                .setMessage("Cannot connect to google api services.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        finish();
                    }
                }).show();
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SnapshotApiActivity.this,ParentActivity.class);
        startActivity(intent);
        finish();
    }
}
