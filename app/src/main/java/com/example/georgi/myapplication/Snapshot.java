package com.example.georgi.myapplication;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

// a java class of singleton type

public class Snapshot extends AppCompatDialog implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SnapshotApis";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth =  FirebaseAuth.getInstance();;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();;
    private DatabaseReference mDatabase = database.getReference().child("DateAboutContextUser");
    FirebaseUser firebaseUser = mAuth.getCurrentUser();;
    private static Context context;

    public FileOutputStream fos;
    private static final String FILE_NAME = "geenou.txt";

    public final DateAboutContextUser dateAboutContextUser = new DateAboutContextUser();

    private static Snapshot ourInstance = null;

    public static String hpp;

    public static Snapshot getInstance(Context context1) {
        context = context1;
        if (ourInstance == null) {
            ourInstance = new Snapshot(context);
        }
        return ourInstance;
    }

    public Snapshot() {
        super(context);
    }

    private Snapshot(Context context) {
        super(context);
    }

    public void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        //Google API client connected. Ready to use awareness api
    }

    public String callSnapShotGroupApis() {
        buildApiClient();
        //display the time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd-MM-yyyy 'at' hh:mm:ss a ");
        String timee = format.format(calendar.getTime());

        //get info about user's current activity
        getCurrentActivity(timee);

        //get the current state of the headphones.
        getHeadphoneStatus(timee);

        //get current location.
        getLocation(timee);

        //get current weather conditions.
        getWeather(timee);
        Log.d(TAG, "Call method API");

        save();
        return timee;
    }


    /*
      Get the current weather condition at current location.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getWeather(final String timee) {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.i(TAG, "Eroare vreme");
                            return;
                        }

                        //parse and display current weather status
                        //to get a Weather instance with the current local weather conditions and make a report
                        Weather weather = weatherResult.getWeather();
                        String weatherReport = "Temperature: " + weather.getTemperature(Weather.CELSIUS)
                                + "\nHumidity: " + weather.getHumidity();
                        int temperature = (int) weather.getTemperature(Weather.CELSIUS);
                        //incercare de a scrie in .txt
                        try {
                            fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_PRIVATE);
                            fos.write(temperature);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dateAboutContextUser.setTemperature(temperature);
                        Log.i("temppp_context", String.valueOf(dateAboutContextUser.getTemperature()));
                        int humidity = weather.getHumidity();

                        try {
                            fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_APPEND);
                            //  Log.i("headphone_context22222", dateAboutContextUser.getHeadphone());
                            fos.write(humidity);
                            fos.write("\n".getBytes());
                            fos.close();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }


                        dateAboutContextUser.setHumidity(humidity);
                        Log.i("humidity_context", String.valueOf(dateAboutContextUser.getHumidity()));
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("temperature (°C)").setValue(temperature);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("humidity").setValue(humidity);
                    }
                });

    }

 /*
      Get user's current location. We are also displaying Google Static map.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")

    private void getLocation(final String timee) {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.i(TAG, "Could not get location.");
                            return;
                        }

                        //get location
                        Location location = locationResult.getLocation();
                        String loc = "Latitudine: "+ location.getLatitude() + ",Longitudine: " + location.getLongitude();
                        float latitude = (float) location.getLatitude();
                        /* try {
                              fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_APPEND);
                                //  Log.i("headphone_context22222", dateAboutContextUser.getHeadphone());
                                fos.write((int)latitude);
                                fos.write("\n".getBytes());
                                fos.close();
                         }  catch (IOException e) {
                             e.printStackTrace();
                         }*/
                        dateAboutContextUser.setLatitude(latitude);
                        float longitude = (float) location.getLongitude();
                        dateAboutContextUser.setLongitude(longitude);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("latitude").setValue(latitude);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("longitude").setValue(longitude);
                    }
                });
    }

    /*
      Check whether the headphones are plugged in or not? This is under snapshot api category.
     */

    private void getHeadphoneStatus(final String timee) {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            Log.i(TAG, "Could not get headphone state.");
                            return;
                        }
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();

                        //get the status
                        String headphoneStatus = headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in." : "Unplugged.";
                        //incercare de a scrie in .txt

                        try {
                            fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_APPEND);
                            //  Log.i("headphone_context22222", dateAboutContextUser.getHeadphone());
                            fos.write(headphoneStatus.getBytes());
                            fos.write("\n".getBytes());
                            fos.close();
                        }  catch (IOException e) {
                            e.printStackTrace();
                        }

                        dateAboutContextUser.setHeadphone(headphoneStatus);
                       // Log.i("georgiiii", dateAboutContextUser.getHeadphone());
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("headphone").setValue(headphoneStatus);


                    }
                    // Log.i("georgiiii", dateAboutContextUser.getHeadphone());
                });
       // Log.i("georgiiii2", dateAboutContextUser.getHeadphone());
       //  Log.i("georgiiii2", hpp);
    }

    /*
      Get current activity of the user. This is under snapshot api category.
      Current activity and confidence level will be displayed on the screen.
     */

    private void getCurrentActivity(final  String timee) {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            //Toast.makeText(SnapshotApiActivity.this, "Could not get the current activity.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();

                        //set the activity name
                        //TextView activityName = (TextView) findViewById(R.id.probable_activity_name);
                        switch (probableActivity.getType()) {
                            case DetectedActivity.IN_VEHICLE:
                                //activityName.setText("In vehicle");
                                dateAboutContextUser.setActivityU("In vehicle");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("In vehicle");
                                break;
                            case DetectedActivity.ON_BICYCLE:
                                //activityName.setText("On bicycle");
                                //act = "On bicycle";
                                dateAboutContextUser.setActivityU("On bicycle");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On bicycle");
                                break;
                            case DetectedActivity.ON_FOOT:
                               // activityName.setText("On foot");
                               // act = "On foot";
                                dateAboutContextUser.setActivityU("On foot");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On foot");
                                break;
                            case DetectedActivity.RUNNING:
                               // activityName.setText("Running");
                                //act = "Running";
                                dateAboutContextUser.setActivityU("Running");
                               mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Running");
                                break;
                            case DetectedActivity.STILL:
                                //activityName.setText("Still");
                                /*
                                try {
                                    fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_APPEND);
                                    //  Log.i("headphone_context22222", dateAboutContextUser.getHeadphone());
                                    fos.write("Still".getBytes());
                                    fos.write("\n".getBytes());
                                    fos.close();
                                }  catch (IOException e) {
                                    e.printStackTrace();
                                }
                                */
                                dateAboutContextUser.setActivityU("Still");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Still");
                                break;
                            case DetectedActivity.UNKNOWN:
                                //activityName.setText("Unknown");
                                //act = "Unknown";
                                dateAboutContextUser.setActivityU("Unknown");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Unknown");
                                break;
                            case DetectedActivity.WALKING:
                                //activityName.setText("Walking");
                                //act = "Walking";
                                dateAboutContextUser.setActivityU("Walking");
                                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Walking");
                                break;
                        }

                        //set the confidante level
                        //ProgressBar confidenceLevel = (ProgressBar) findViewById(R.id.probable_activity_confidence);
                        //confidenceLevel.setProgress(probableActivity.getConfidence());

                        //display the time
                        //TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm:ss a ", Locale.getDefault());
                        //timeTv.setText("Time & Date: " + sdf.format(new Date(ar.getTime())));
                        dateAboutContextUser.setTime(sdf);
                        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("time").setValue(sdf.format(new Date(ar.getTime())));
                    }
                });
    }

    public void save () {
        //String text = activityName.getText().toString();
        try {
            fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_APPEND);
            Log.i("PROBLEMAAA", dateAboutContextUser.toString());
          //  Log.i("headphone_context22222", dateAboutContextUser.getHeadphone());
//            fos.write(dateAboutContextUser.getHeadphone().getBytes());
       //     fos.write("\n".getBytes());
            fos.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


        @Override
    public void onConnectionSuspended(final int i) {
        new AlertDialog.Builder( context)
                .setMessage("Cannot connect to google api services.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        //context.getApplicationContext().finish();
                    }
                }).show();
    }

}

