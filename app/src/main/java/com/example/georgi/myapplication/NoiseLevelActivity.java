package com.example.georgi.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NoiseLevelActivity extends AppCompatActivity {

    /* constants - interval time to record */
    private static final int POLL_INTERVAL = 3000;

    private  int threshold_silently = -10;
    private  int threshold_slowly = 5;
    private int threshold_normal = 15;
    private int threshold_loudly = 25;

    /** running state **/
    private boolean mRunning = false;

    /** config state **/
    private int mThreshold;

    int RECORD_AUDIO = 0;
    private PowerManager.WakeLock mWakeLock;

    private Handler mHandler = new Handler();

    /* References to view elements */
    private TextView mStatusView,tv_noice, type_noise;

    /* sound data source */
    private DetectNoise mSensor;
    ProgressBar bar;
    /****************** Define runnable thread again and again detect noise *********/

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            //Log.i("Noise", "runnable mSleepTask");
            start();
        }
    };

    // Create runnable thread to Monitor Voice
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            //Log.i("Noise", "runnable mPollTask");
            updateDisplay("Listening...", amp);

            if ((amp > mThreshold)) {
                callForHelp(amp);
                //Log.i("Noise", "==== onCreate ===");
            }
            // Runnable(mPollTask) will again execute after POLL_INTERVAL
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Defined SoundLevelView in main.xml file
        setContentView(R.layout.activity_noise_level);
        mStatusView = (TextView) findViewById(R.id.status);
        tv_noice=(TextView)findViewById(R.id.tv_noice);
        type_noise = (TextView)findViewById(R.id.tv_type_noise);
        bar=(ProgressBar)findViewById(R.id.progressBar1);
        // Used to record voice
        mSensor = new DetectNoise();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "NoiseAlert");

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
                        Intent intent0 = new Intent(NoiseLevelActivity.this, MainActivity.class);
                        startActivity(intent0);
                        break;
                    case R.id.nav_map:
                        Intent intent1 = new Intent(NoiseLevelActivity.this, MapActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_noise:

                        break;
                    case R.id.nav_coordinates:
                        Intent intent3 = new Intent(NoiseLevelActivity.this, RequestActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_notification:
                        Intent intent4 = new Intent(NoiseLevelActivity.this, KmeansActivity.class);
                        startActivity(intent4);
                        break;

                }

                return false;
            }
        });

        /* end bottom menu */
    }
    @Override
    public void onResume() {
        super.onResume();
        //Log.i("Noise", "==== onResume ===");

        initializeApplicationConstants();
        if (!mRunning) {
            mRunning = true;
            start();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        // Log.i("Noise", "==== onStop ===");
        //Stop noise monitoring
        stop();
    }
    private void start() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        }

        //Log.i("Noise", "==== start ===");
        mSensor.start();
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        //Noise monitoring start
        // Runnable(mPollTask) will execute after POLL_INTERVAL
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }
    private void stop() {
        Log.d("Noise", "==== Stop Noise Monitoring===");
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        bar.setProgress(0);
        updateDisplay("stopped...", 0.0);
        mRunning = false;

    }


    private void initializeApplicationConstants() {
        // Set Noise Threshold
        mThreshold = 8;

    }

    private void updateDisplay(String status, double signalEMA) {
        mStatusView.setText(status);
        //
        bar.setProgress((int)signalEMA);
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
        if(signalEMA < threshold_silently ){
            type_noise.setText("Level noise around is: Silently");
        }
        if((signalEMA > threshold_silently) && (signalEMA < threshold_slowly)){
            type_noise.setText("Level noise around is: Slowly");
        }
        if((signalEMA > threshold_slowly) && (signalEMA < threshold_normal)){
            type_noise.setText("Level noise around is: Normal");
        }
        if((signalEMA > threshold_normal) && (signalEMA < threshold_loudly)){
            type_noise.setText("Level noise around is: Loudly");
        }
        if(signalEMA > threshold_loudly){
            type_noise.setText("Level noise around is: Very loudly");
        }
    }


    private void callForHelp(double signalEMA) {

        //stop();

        // Show alert when noise thersold crossed
        Toast.makeText(getApplicationContext(), "Noise Thersold Crossed, do here your stuff.",
                Toast.LENGTH_LONG).show();
        Log.d("SONUND", String.valueOf(signalEMA));
        tv_noice.setText(signalEMA+"dB");
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NoiseLevelActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}

