package com.example.georgi.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextDataActivity extends AppCompatActivity {

    private TextView date_time_tv, activity_tv, time_sec_tv, date_format_tv;
    private TextView weather_tv, location_tv, headphone_tv, time_slot_tv;
    Integer activityDb = 0;
    Integer headphoneStatusDb = 0;

    SimpleDateFormat format_day = new SimpleDateFormat("yyyy-MM-dd");
    String day = format_day.format(new Date());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DataAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(day);

    Long time_secDb;
    Long timeFormatDb;
    Double timeSlotDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_data);

        date_time_tv = (TextView)findViewById(R.id.date_time_tv);
        activity_tv = (TextView)findViewById(R.id.activity_tv);
        time_sec_tv = (TextView)findViewById(R.id.time_sec_tv);
        time_slot_tv = (TextView)findViewById(R.id.time_slot_tv);
        date_format_tv = (TextView)findViewById(R.id.date_format_tv);
        headphone_tv = (TextView)findViewById(R.id.headphone_tv);
        weather_tv = (TextView)findViewById(R.id.weather_tv);
        location_tv = (TextView)findViewById(R.id.location_tv);
        displayDataContext();

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
                        Intent intent0 = new Intent(ContextDataActivity.this, MainActivity.class);
                        startActivity(intent0);
                        break;
                    case R.id.nav_map:
                        Intent intent1 = new Intent(ContextDataActivity.this, MapActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_noise:
                        Intent intent2 = new Intent(ContextDataActivity.this, NoiseLevelActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_coordinates:
                        Intent intent3 = new Intent(ContextDataActivity.this, RequestActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.nav_notification:
                        Intent intent4 = new Intent(ContextDataActivity.this, KmeansActivity.class);
                        startActivity(intent4);
                        break;

                }

                return false;
            }
        });

        /* end bottom menu */

    }

    public void displayDataContext(){

        mDatabase.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Log.i("User key", dataSnapshot.getKey());
                    //display current time
                    String time_dateDb = dataSnapshot.getKey();
                    date_time_tv.setText(time_dateDb);

                    //display user's activity
                    if(dataSnapshot.child("activity").getValue(Integer.class) != null) {
                        activityDb = dataSnapshot.child("activity").getValue(Integer.class).intValue();
                    }
                    String activityDb_type = "";
                    if(activityDb == 1){
                        activityDb_type = "Still";
                    }
                    else
                    if(activityDb == 2){
                        activityDb_type = "Unknown";
                    }
                    else
                    if(activityDb == 3){
                        activityDb_type = "In vehicle";
                    }
                    else
                    if(activityDb == 4){
                        activityDb_type = "On bicycle";
                    }
                    else
                    if(activityDb == 5){
                        activityDb_type = "On foot";
                    }
                    else
                    if(activityDb == 6){
                        activityDb_type = "Running";
                    }
                    else
                    if(activityDb == 7){
                        activityDb_type = "Walking";
                    }
                    activity_tv.setText(activityDb_type);

                    //display time in seconds
                    if(dataSnapshot.child("timestamp").getValue(Long.class) != null){
                        time_secDb = dataSnapshot.child("timestamp").getValue(Long.class);
                    }

                    time_sec_tv.setText(time_secDb + " seconds");

                    //display date format
                    if(dataSnapshot.child("time").getValue(Long.class) != null ){
                        timeFormatDb = dataSnapshot.child("time").getValue(Long.class);
                    }
                    String day_week_string = "null";
                    String month_string = "null";
                    int day_week = (int) (timeFormatDb/10000);
                    int day = (int) ((timeFormatDb%10000)/100);
                    int month = (int) (timeFormatDb%100);
                    if(month < 10){
                        month = month % 10;
                    }
                    if(day_week == 1){
                        day_week_string = "Sunday, ";
                    }
                    if(day_week == 2){
                        day_week_string = "Monday, ";
                    }
                    if(day_week == 3){
                        day_week_string = "Tuesday, ";
                    }
                    if(day_week == 4){
                        day_week_string = "Wednesday, ";
                    }
                    if(day_week == 5){
                        day_week_string = "Thursday, ";
                    }
                    if(day_week == 6){
                        day_week_string = "Friday, ";
                    }
                    if(day_week == 7){
                        day_week_string = "Saturday, ";
                    }
                    if(month == 1){
                        month_string = " January";
                    }
                    if(month == 2){
                        month_string = " February";
                    }
                    if(month == 3){
                        month_string = " March";
                    }
                    if(month == 4){
                        month_string = " April";
                    }
                    if(month == 5){
                        month_string = " May";
                    }
                    if(month == 6){
                        month_string = " June";
                    }
                    if(month == 7){
                        month_string = " July";
                    }
                    if(month == 8){
                        month_string = " August";
                    }
                    if(month == 9){
                        month_string = " September";
                    }
                    if(month == 10){
                        month_string = " Octomber";
                    }
                    if(month == 11){
                        month_string = " November";
                    }
                    if(month == 12) {
                        month_string = " December";
                    }


                    date_format_tv.setText(day_week_string + String.valueOf(day) + month_string);

                    //display time slot
                    if(dataSnapshot.child("timeslot").getValue(Double.class) != null){
                        timeSlotDb = dataSnapshot.child("timeslot").getValue(Double.class);
                    }

                    time_slot_tv.setText("" + timeSlotDb);

                    //display status phone
                    if(dataSnapshot.child("headphone").getValue(Integer.class) != null){
                        headphoneStatusDb = dataSnapshot.child("headphone").getValue(Integer.class).intValue();
                    }

                    if(headphoneStatusDb == 1){
                        headphone_tv.setText("Plugged in");
                    }
                    else
                    {
                        headphone_tv.setText("Unplugged");
                    }

                    //dispaly location in latitude and longitude
                    Double latitudeDb = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitudeDb = dataSnapshot.child("longitude").getValue(Double.class);
                    String locationDb = "Latitude: " + latitudeDb + "\nLongitude: " + longitudeDb;
                    location_tv.setText(locationDb);

                    //display weather
                    Double temperatureDb = dataSnapshot.child("temperature (°C)").getValue(Double.class);
                    Double humdityDb = dataSnapshot.child("humidity").getValue(Double.class);
                    String weatherDb = "Temperature: " + temperatureDb + " °C \nHumidity: " + humdityDb + " %";
                    weather_tv.setText(weatherDb);


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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ContextDataActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
