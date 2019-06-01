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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// a java class of singleton type

public class Snapshot extends AppCompatDialog implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SnapshotApis";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth =  FirebaseAuth.getInstance();;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();;
    private DatabaseReference mDatabase = database.getReference().child("DataAboutContextUser");
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

    DataAboutContextUser dataAboutContextUser = new DataAboutContextUser();
    String timee;
    String day;


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
        SimpleDateFormat format_time = new SimpleDateFormat("HH:mm:ss");
        timee = format_time.format(calendar.getTime());
        SimpleDateFormat format_day = new SimpleDateFormat("yyyy-MM-dd");
        day = format_day.format(new Date());
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
                        dataAboutContextUser.setTemperature(temperature);
                        Log.i("temppp_context", String.valueOf(dataAboutContextUser.getTemperature()));
                        int humidity = weather.getHumidity();


                        dataAboutContextUser.setHumidity(humidity);
                        flag = flag | bit1;
                        Log.i("humidity_context", String.valueOf(dataAboutContextUser.getHumidity()));
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
                        dataAboutContextUser.setLatitude(latitude);
                        float longitude = (float) location.getLongitude();
                        dataAboutContextUser.setLongitude(longitude);
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
                        int headphoneStatus = headphoneState.getState() == HeadphoneState.PLUGGED_IN ? 1 : 0;
                        dataAboutContextUser.setHeadphone(headphoneStatus);
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
                            case DetectedActivity.STILL:
                                dataAboutContextUser.setActivityU(1);
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Still");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.UNKNOWN:
                                dataAboutContextUser.setActivityU(2);
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Unknown");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.IN_VEHICLE:
                                dataAboutContextUser.setActivityU(3);
                                flag = flag | bit4;
                               // mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("In vehicle");
                                break;

                            case DetectedActivity.ON_BICYCLE:
                                dataAboutContextUser.setActivityU(4);
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On bicycle");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.ON_FOOT:
                                dataAboutContextUser.setActivityU(5);
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("On foot");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.RUNNING:
                                dataAboutContextUser.setActivityU(6);
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Running");
                                flag = flag | bit4;
                                break;

                            case DetectedActivity.WALKING:
                                dataAboutContextUser.setActivityU(7);
                                flag = flag | bit4;
                                //mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(timee).child("activity").setValue("Walking");
                                break;
                        }

                        //get the time
                        SimpleDateFormat sdf = new SimpleDateFormat(" hh:mm:ss a ", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        long timestamp = calendar.getTimeInMillis();
                        long data = calendar.getTimeInMillis() / 1000; //time in seconds
                        //convert timestamp in format like DAY_OF_WEEK DAY_OF_MONTH MONTH MONTH_OF_YEAR

                        int day_week = calendar.get(Calendar.DAY_OF_WEEK);  //sunday = 1
                        int day_month = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int month = calendar.get(Calendar.MONTH) + 1; // months start from 0 to 11
                        long timeFormat = (day_week*100 + day_month)*100 + month;
                        Log.i("ora", String.valueOf(hour));
                        double timeSlot;
                        int minutes = calendar.get(Calendar.MINUTE);
                        if(minutes >= 0 && minutes <= 30){
                            timeSlot = hour;
                        }
                        else
                        {
                            timeSlot = hour + 0.5;
                        }
                        Log.i("minute", String.valueOf(minutes));
                        Log.i("TimeSlot", String.valueOf(timeSlot));
                        dataAboutContextUser.setTimeFormat(timeFormat);
                        dataAboutContextUser.setTimestamp(data);
                        dataAboutContextUser.setTimeSlot(timeSlot);
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
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("temperature (°C)").setValue(dataAboutContextUser.getTemperature());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("humidity").setValue(dataAboutContextUser.getHumidity());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("latitude").setValue(dataAboutContextUser.getLatitude());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("longitude").setValue(dataAboutContextUser.getLongitude());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("headphone").setValue(dataAboutContextUser.getHeadphone());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("activity").setValue(dataAboutContextUser.getActivityU());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("timestamp").setValue(dataAboutContextUser.getTimestamp());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("time").setValue(dataAboutContextUser.getTime());
                mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day).child(timee).child("timeslot").setValue(dataAboutContextUser.getTimeSlot());
                flag = 0x00;
                String contextData = dataAboutContextUser.getActivityU() + "," + dataAboutContextUser.getTime() + "," +
                                    dataAboutContextUser.getTimestamp() + "," + dataAboutContextUser.getTimeSlot() + "," +
                                    dataAboutContextUser.getHeadphone() + "," + dataAboutContextUser.getLatitude() + "," +
                                    dataAboutContextUser.getLongitude() + "," + dataAboutContextUser.getHumidity() + "," +
                                    dataAboutContextUser.getTemperature() + "\n";

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


