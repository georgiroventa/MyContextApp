package com.example.georgi.myapplication;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class SnapshotApiActivity extends AppCompatActivity {

    //tags
    private String LOG_TAG = "Status directory";
    private Button b5;
    private TextView tl;
    private SensorManager sensorManager;
    private Sensor light;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseDatabase database;

    FirebaseUser firebaseUser;
    String time;

    //data about user
    DateAboutContextUser dataUser;


    SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
    String dayWeek = format_day.format(new Date());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DateAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_api);


        //instantiate dataUser reference
        dataUser = new DateAboutContextUser();


        if (mAuth.getCurrentUser() != null) {
            //firebaseUser= mAuth.getCurrentUser();
            String uid = mAuth.getCurrentUser().getUid();
            Log.i("currentUser",uid);


            b5 = (Button) findViewById(R.id.button_light);

            tl = (TextView) findViewById(R.id.textView_light);

            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                    tl.setText("" + light);
                }
            });

            //Snapshot sa = Snapshot.getInstance(getApplicationContext());
            //time = sa.callSnapShotGroupApis();


            //startService();
            displayData();

        }
    }

/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //displayData();
    }
*/

    public void displayData(){
        Log.i("display: ", "sunt aici");

            mDatabase.limitToLast(1).addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    //set the activity name
                    //prevChildKey = "oiii";
                    //Log.i("PrevChild", prevChildKey);
                    TextView activityTv = (TextView) findViewById(R.id.probable_activity_name);
                    String activityDb = dataSnapshot.child("activity").getValue(String.class);
                    dataUser.setActivityU(activityDb);
                    activityTv.setText(activityDb);

                    //display the location
                    TextView longitudeTv = (TextView) findViewById(R.id.longitude_status);
                    Double longitudeDb = dataSnapshot.child("longitude").getValue(Double.class);
                    String longitudeSt = longitudeDb + "";
                    Log.i("Aiciiiiiiiiiiii", longitudeSt);
                    longitudeTv.setText("Longitudine: " + longitudeDb);
                    TextView latitudeTv = (TextView) findViewById(R.id.latitude_status);
                    Double latitudeDb = dataSnapshot.child("latitude").getValue(Double.class);
                    latitudeTv.setText("Latitudine: " + latitudeDb);

                    //display the weather
                    TextView humidityTv = (TextView) findViewById(R.id.humidity_status);
                    Double humidityDb = dataSnapshot.child("humidity").getValue(Double.class);
                    humidityTv.setText("Humidity: " + humidityDb);
                    TextView temperatureTv = (TextView) findViewById(R.id.temperature_status);
                    Double temperatureDb = dataSnapshot.child("temperature (°C)").getValue(Double.class);
                    temperatureTv.setText("Temperature: " + temperatureDb);

                    //display the time in seconds
                    TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                    String timeDb = dataSnapshot.child("time").getValue().toString();
                    Log.i("Time in seconds", timeDb);
                    timeTv.setText("Time & Date: " + timeDb);

                    //display the status
                    TextView headphoneStatusTv = (TextView)findViewById(R.id.headphone_status);
                    String headphoneStatusDb = dataSnapshot.child("headphone").getValue().toString();
                    headphoneStatusTv.setText(headphoneStatusDb);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("EEEE, dd-MM-yyyy 'at' hh:mm:ss a ");
                    String currentTime = format.format(calendar.getTime());
                    Log.i("Timpul curent", currentTime);

                    //display the current time
                    TextView currentTimeTv = (TextView)findViewById(R.id.current_time);
                    //String currentTimeDb = dataSnapshot.getValue().toString();
                    currentTimeTv.setText(currentTime);


                    //Load the current map image from Google map
                    String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                            + longitudeDb + "," + latitudeDb
                            + "&zoom=20&size=400x250&key=" + getString(R.string.google_maps_key);  // key_api = google_maps_key
                    Picasso.with(SnapshotApiActivity.this).load(url).into((ImageView) findViewById(R.id.current_map));

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });






    }



    public void startService() {

        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Application still running");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    //check if location is enable, if not open the settings
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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SnapshotApiActivity.this,ParentActivity.class);
        startActivity(intent);
        finish();
    }
}

