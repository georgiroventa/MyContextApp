package com.example.georgi.myapplication;

import android.location.Location;
import android.os.Environment;
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
import java.io.File;
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

    //create csv in external storage
    public  File FILE_NAME = getPublicAlbumStorageDir("TestFile");
    public FileOutputStream fos;

    {
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ContextApp/" + "/TestFile/" + "DataContextClient.csv");
            if (!f.exists()) {
                f.createNewFile();
                Log.i("FileCreated", "file is created");
            }

            fos = new FileOutputStream(f, true);
            fos.write("activity, time, headphoneStatus, latitude, longitude, humidity, temperature\n".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    int flag = 0x00;
    final int bit1 = 0x01;
    final int bit2 = 0x02;
    final int bit3 = 0x04;
    final int bit4 = 0x08;
    final int bit5 = 0x10;

    DateAboutContextUser dateAboutContextUser = new DateAboutContextUser();
    String timee;
    String dayWeek;


    private static Snapshot ourInstance = null;
    private String LOG_TAG = "Status directory";

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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format_time = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss  ");
        timee = format_time.format(calendar.getTime());
        SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
        dayWeek = format_day.format(new Date());
        Log.i("Zi din saptamana", dayWeek);
        buildApiClient();

        //get info about user's current activity
        getCurrentActivity();

        //get the current state of the headphones.
        getHeadphoneStatus();

        //get current location.
        getLocation();

        //get current weather conditions.
        getWeather();
        Log.d(TAG, "Call method API");

        return timee;
    }


    /*
      Get the current weather condition at current location.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getWeather() {
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
                        dateAboutContextUser.setTemperature(temperature);
                        Log.i("temppp_context", String.valueOf(dateAboutContextUser.getTemperature()));
                        int humidity = weather.getHumidity();


                        dateAboutContextUser.setHumidity(humidity);
                        flag = flag | bit1;
                        Log.i("humidity_context", String.valueOf(dateAboutContextUser.getHumidity()));
                        save();
                        //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("temperature (°C)").setValue(temperature);
                        //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("humidity").setValue(humidity);
                    }
                });

    }

 /*
      Get user's current location. We are also displaying Google Static map.
     */
    //@RequiresPermission("android.permission.ACCESS_FINE_LOCATION")

    private void getLocation() {
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
                        dateAboutContextUser.setLatitude(latitude);
                        float longitude = (float) location.getLongitude();
                        dateAboutContextUser.setLongitude(longitude);
                        flag = flag | bit2;
                        save();
                        //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("latitude").setValue(latitude);
                        //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("longitude").setValue(longitude);
                    }
                });
    }

    /*
      Check whether the headphones are plugged in or not? This is under snapshot api category.
     */

    private void getHeadphoneStatus() {
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
                        String headphoneStatus = headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in" : "Unplugged";
                        dateAboutContextUser.setHeadphone(headphoneStatus);
                        flag = flag | bit3;
                        save();
                       // mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("headphone").setValue(headphoneStatus);


                    }
                });
    }

    /*
      Get current activity of the user. This is under snapshot api category.
      Current activity and confidence level will be displayed on the screen.
     */

    private void getCurrentActivity() {
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
                        switch (probableActivity.getType()) {
                            case DetectedActivity.IN_VEHICLE:
                                dateAboutContextUser.setActivityU("In vehicle");
                                flag = flag | bit4;
                               // mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("In vehicle");
                                break;

                            case DetectedActivity.ON_BICYCLE:
                                dateAboutContextUser.setActivityU("On bicycle");
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On bicycle");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.ON_FOOT:
                                dateAboutContextUser.setActivityU("On foot");
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On foot");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.RUNNING:
                                dateAboutContextUser.setActivityU("Running");
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Running");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.STILL:
                                dateAboutContextUser.setActivityU("Still");
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Still");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.UNKNOWN:
                                dateAboutContextUser.setActivityU("Unknown");
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Unknown");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.WALKING:
                                dateAboutContextUser.setActivityU("Walking");
                                flag = flag | bit4;
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Walking");
                                break;
                        }

                        //get the time
                        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm:ss a ", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        long data = calendar.getTimeInMillis() / 1000;
                        dateAboutContextUser.setTimeSeconds(data);
                        flag = flag | bit5;
                        save();
                        //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("time").setValue(sdf);
                    }
                });
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.i("State", "Yes, it is writable!");
            return true;
        }
        return false;
    }


    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                "ContextApp"), albumName);
        Log.i("directory", "directory is created");
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return  file;
    }



        public void save () {
            Log.i("Flag", String.valueOf(flag));
            if( flag == 31 ){
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("temperature (°C)").setValue(dateAboutContextUser.getTemperature());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("humidity").setValue(dateAboutContextUser.getHumidity());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("latitude").setValue(dateAboutContextUser.getLatitude());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("longitude").setValue(dateAboutContextUser.getLongitude());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("headphone").setValue(dateAboutContextUser.getHeadphone());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("activity").setValue(dateAboutContextUser.getActivityU());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek).child(timee).child("time").setValue(dateAboutContextUser.getTime());
                flag = 0x00;
                String contextData = dateAboutContextUser.getActivityU() + "," + dateAboutContextUser.getTime() + "," +
                                    dateAboutContextUser.getHeadphone() + "," + dateAboutContextUser.getLatitude() + "," +
                                    dateAboutContextUser.getLongitude() + "," + dateAboutContextUser.getHumidity() + "," +
                                    dateAboutContextUser.getTemperature() + "\n";

                try {
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/ContextApp/" + "/TestFile/" + "DataContextClient.csv" );
                    if (!f.exists()) {
                        f.createNewFile();
                        Log.i("FileCreated", "file is created");
                    }
                    Log.i("fisier_", "intra aici");
                    fos = new FileOutputStream(f, true);
                    fos.write(contextData.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {

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


