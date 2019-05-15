package com.example.georgi.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextDataActivity extends AppCompatActivity {

    private TextView date_time_tv, activity_tv, time_sec_tv, date_format_tv;
    private TextView weather_tv, location_tv, headphone_tv, time_slot_tv;
    private Button button;

    SimpleDateFormat format_day = new SimpleDateFormat("EEEE");
    String dayWeek = format_day.format(new Date());
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("DateAboutContextUser").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(dayWeek);


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
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayDataContext();
            }
        });
        displayDataContext();

    }

    public void displayDataContext(){

        mDatabase.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Log.i("User key", dataSnapshot.getKey());
                    //display current time
                    String time_dateDb = dataSnapshot.getKey();
                    date_time_tv.setText("Date and current time: " + time_dateDb);

                    //display user's activity
                    String activityDb = dataSnapshot.child("activity").getValue(String.class);
                    activity_tv.setText("User's activity: " + activityDb);

                    //display time in seconds
                    Long time_secDb = dataSnapshot.child("timestamp").getValue(Long.class);
                    time_sec_tv.setText("Time in seconds: " + time_secDb);

                    //display date format
                    Long timeFormatDb = dataSnapshot.child("time").getValue(Long.class);
                    date_format_tv.setText("Date(dayOfWeek_dayOfMonth_month): " + timeFormatDb);

                    //display time slot
                    Double timeSlotDb = dataSnapshot.child("timeslot").getValue(Double.class);
                    time_slot_tv.setText("Time slot: " + timeSlotDb);

                    //display status phone
                    String headphoneDb = dataSnapshot.child("headphone").getValue(String.class);
                    headphone_tv.setText("Headphone status: " + headphoneDb);

                    //dispaly location in latitude and longitude
                    Double latitudeDb = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitudeDb = dataSnapshot.child("longitude").getValue(Double.class);
                    String locationDb = "Latitude: " + latitudeDb + "\nLongitude: " + longitudeDb;
                    location_tv.setText("Current location\n" + locationDb);

                    //display weather
                    Double temperatureDb = dataSnapshot.child("temperature (°C)").getValue(Double.class);
                    Double humdityDb = dataSnapshot.child("humidity").getValue(Double.class);
                    String weatherDb = "Temperature: " + temperatureDb + " °C \nHumidity: " + humdityDb + " %";
                    weather_tv.setText("Current weather\n" + weatherDb);


                    Log.i("User activity", dataSnapshot.child("activity").getValue().toString());
                    Log.i("Time in seconds", dataSnapshot.child("time").getValue().toString());

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
        Intent intent=new Intent(ContextDataActivity.this,ParentActivity.class);
        startActivity(intent);
        finish();
    }
}
