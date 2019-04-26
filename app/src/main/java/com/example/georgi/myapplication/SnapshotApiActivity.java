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
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class
SnapshotApiActivity extends AppCompatActivity /*implements GoogleApiClient.ConnectionCallbacks*/{


    private static final String TAG = "SnapshotActivity";
    private GoogleApiClient mGoogleApiClient;

    private Button b1, b2, b3, b4, b5, b6;
    private TextView tl;

    private SensorManager sensorManager;
    private Sensor light;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    public static Integer index = 0;
    String time;
    private static final String FILE_NAME = "test.txt";


    public TextView activityName;
    public TextView timeTv;
    public TextView locTv;
    public TextView headphoneStatusTv;
    public TextView temperatureTv;

    /*public String activity;
    public String some;
    public String location;
    public String headphoneStatus;
    public String weather;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot_api);

        mAuth = FirebaseAuth.getInstance();
        //firebaseUser = mAuth.getCurrentUser();
        //get a database reference
        //database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("DateAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        if (mAuth.getCurrentUser() != null) {
            //firebaseUser= mAuth.getCurrentUser();
            String uid = mAuth.getCurrentUser().getUid();
            Log.i("currentUser",uid);

            b1 = (Button) findViewById(R.id.button_activity);
            b2 = (Button) findViewById(R.id.button_headphone);
            b3 = (Button) findViewById(R.id.button_location);
            b4 = (Button) findViewById(R.id.button_weather);
            b5 = (Button) findViewById(R.id.button_light);
            b6 = (Button) findViewById(R.id.button_all_apis);

            tl = (TextView) findViewById(R.id.textView_light);

            activityName = findViewById(R.id.probable_activity_name);
            timeTv = findViewById(R.id.probable_activity_time);
            locTv = findViewById(R.id.current_latlng);
            headphoneStatusTv = findViewById(R.id.headphone_status);
            temperatureTv = findViewById(R.id.weather_status);

            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                    tl.setText("" + light);
                }
            });

            Snapshot sa = Snapshot.getInstance(getApplicationContext());
            time = sa.callSnapShotGroupApis();
            displayData();
            scheduleJob();

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }



    public void displayData(){
        Log.i("display: ", "sunt aici");
        FileOutputStream fos = null;
        //Log.i("Database", String.valueOf(mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild().limitToLast(1)));
        // Attach a listener to read the data at our posts reference
        //orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1)

        mDatabase.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //set the activity name
                //TextView activityName = (TextView) findViewById(R.id.probable_activity_name);
                String activity = dataSnapshot.child("activity").getValue().toString();
                activityName.setText(activity);
                //Log.i("aiciiiiiiiiiii", activity);

                //display the time
                //TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                String some = dataSnapshot.child("time").getValue().toString();
                timeTv.setText("Time & Date: " + some);
                //Log.i("aiciiiiii_timp",some);

                //display the location
                //TextView locTv = (TextView) findViewById(R.id.current_latlng);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                String location = "Latitude: " + latitude + "\n" +
                        "Longitude: " + longitude;
                locTv.setText(location);
                Log.i("locationnnn", location);
                // Log.i("aici_longi", String.valueOf(longitude));

                //display the status
                //TextView headphoneStatusTv = (TextView)findViewById(R.id.headphone_status);
                String headphoneStatus = dataSnapshot.child("headphone").getValue().toString();
                headphoneStatusTv.setText(headphoneStatus);

                //display the weather
                //TextView temperatureTv = (TextView) findViewById(R.id.weather_status);
                Double temperature = dataSnapshot.child("temperature (°C)").getValue(Double.class);
                Double humidity = dataSnapshot.child("humidity").getValue(Double.class);
                String weather = "Temperature: " + temperature + "\nhumidity: " + humidity;
                temperatureTv.setText(weather);
                Log.i("aicii_temp", weather);

                //Load the current map image from Google map
                String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                        + longitude + "," + latitude
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
            String text = activityName.getText().toString();
            Log.i("prostuta: ", text);
                try {
                    fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    fos.write(text.getBytes());

                    Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                            Toast.LENGTH_LONG).show();
                    //fos.write(text);
                    //fos.
                    //fos.write(some.getBytes());
                    //fos.write(location.getBytes());
                    //fos.write(headphoneStatus.getBytes());
                    //fos.write(weather.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


    }




    //jobScheduler
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ColectJobService.class);
       // PersistableBundle bundle = new PersistableBundle();
        Log.d(TAG, String.valueOf(mGoogleApiClient));
        //bundle.putString("mGoogleApi", String.valueOf(mGoogleApiClient));

        JobInfo info = new JobInfo.Builder(123, componentName)
                //.setRequiresCharging(true)
               // .setExtras(bundle)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
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
















    /*
      Get user's current location. We are also displaying Google Static map.
     */
//@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    /*
    private void getLocation(final String timee) {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Toast.makeText(SnapshotApiActivity.this, "Could not get location.", Toast.LENGTH_SHORT).show();
                           // statusCheck();
                            //Toast.makeText(SnapshotApiActivity.this, "Eroare.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //get location
                        Location location = locationResult.getLocation();
                        String loc = "Latitudine: "+ location.getLatitude() + ",Longitudine: " + location.getLongitude();
                        float latitude = (float) location.getLatitude();
                        float longitude = (float) location.getLongitude();
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("latitude").setValue(latitude);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("longitude").setValue(longitude);
                        ((TextView) findViewById(R.id.current_latlng)).setText(loc);

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.latlng_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy 'at' hh:mm:ss a ", Locale.getDefault());
                        timeTv.setText("Time & Date: " + sdf.format(new Date(location.getTime())));

                        //Load the current map image from Google map
                        String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                                + location.getLatitude() + "," + location.getLongitude()
                                + "&zoom=20&size=400x250&key=" + getString(R.string.google_maps_key);  // key_api = google_maps_key
                        Picasso.with(SnapshotApiActivity.this).load(url).into((ImageView) findViewById(R.id.current_map));
                    }
                });
    }
    */

    /*
      Check whether the headphones are plugged in or not? This is under snapshot api category.
     */
    /*
    private void getHeadphoneStatus(final String timee) {
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
                        String headphoneStatus = headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in." : "Unplugged.";
                        headphoneStatusTv.setText(headphoneStatus);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("headphone").setValue(headphoneStatus);
                    }
                });
    }
*/
    /*
      Get current activity of the user. This is under snapshot api category.
      Current activity and confidence level will be displayed on the screen.
     */
    /*
    private void getCurrentActivity(final  String timee) {
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
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("In vehicle");
                                break;
                            case DetectedActivity.ON_BICYCLE:
                                activityName.setText("On bicycle");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On bicycle");
                                break;
                            case DetectedActivity.ON_FOOT:
                                activityName.setText("On foot");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On foot");
                                break;
                            case DetectedActivity.RUNNING:
                                activityName.setText("Running");
                               mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Running");
                                break;
                            case DetectedActivity.STILL:
                                activityName.setText("Still");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Still");
                                break;
                            case DetectedActivity.UNKNOWN:
                                activityName.setText("Unknown");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Unknown");
                                break;
                            case DetectedActivity.WALKING:
                                activityName.setText("Walking");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Walking");
                                break;
                        }
                        //set the confidante level
                        ProgressBar confidenceLevel = (ProgressBar) findViewById(R.id.probable_activity_confidence);
                        confidenceLevel.setProgress(probableActivity.getConfidence());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm:ss a ", Locale.getDefault());
                        timeTv.setText("Time & Date: " + sdf.format(new Date(ar.getTime())));
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("time").setValue(sdf.format(new Date(ar.getTime())));
                    }
                });
    } */

    /*@Override
    public void onConnectionSuspended(final int i) {
        new AlertDialog.Builder(this)
                .setMessage("Cannot connect to google api services.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        finish();
                    }
                }).show();
    }*/
